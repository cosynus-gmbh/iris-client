package iris.client_bff.kir_tracing.eps;

import com.bitbucket.thinbus.srp6.js.*;
import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6Exception;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import iris.client_bff.IrisWebIntegrationTest;
import iris.client_bff.config.SrpParamsConfig;
import iris.client_bff.kir_tracing.IncomingKirConnection;
import iris.client_bff.kir_tracing.IncomingKirConnectionRepository;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingFormRepository;
import iris.client_bff.proxy.EPSProxyServiceServiceClient;
import iris.client_bff.vaccination_info.EncryptedConnectionsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static com.nimbusds.srp6.BigIntegerUtils.toHex;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static iris.client_bff.kir_tracing.eps.KirTracingTestData.*;
import static iris.client_bff.matchers.IrisMatchers.isCatWith;
import static iris.client_bff.matchers.IrisMatchers.isUuid;
import static iris.client_bff.vaccination_info.eps.VaccinationInfoAnnouncmentTestData.VALID_ANNOUNCEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.HamcrestCondition.matching;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@IrisWebIntegrationTest
@RequiredArgsConstructor
@Tag("kir-tracing")
@Tag("json-rpc-controller")
@DisplayName("IT of JSON-RPC controller for kir tracing")
class KirTracingEpsTest {

    final private SrpParamsConfig srpParamsConfig;

    static final String JSON_RPC = "application/json-rpc";

    final EncryptedConnectionsService encryptedConnectionsService;
    final PasswordEncoder passwordEncoder;

    final IncomingKirConnectionRepository incomingConnections;
    final KirTracingFormRepository kirTracingForms;

    final MockMvc mvc;

    @SpyBean
    final EPSProxyServiceServiceClient proxyClient;

    @Captor
    ArgumentCaptor<Instant> announcementCaptor;

    @BeforeEach
    void setUp() {

        Locale.setDefault(Locale.ENGLISH);

        RestAssuredMockMvc.requestSpecification = new MockMvcRequestSpecBuilder()
                .setMockMvc(mvc)
                .setContentType(JSON_RPC).build();

        RestAssuredMockMvc.responseSpecification = new ResponseSpecBuilder()
                .expectContentType(JSON_RPC)
                .registerParser(JSON_RPC, Parser.JSON)
                .expectBody("id", equalTo("1"))
                .build();
    }

    @AfterAll
    void tearDown() {
        RestAssuredMockMvc.requestSpecification = null;
        RestAssuredMockMvc.responseSpecification = null;
    }

    @Test
    @DisplayName("request connection parameters: valid JSON ⇒ 💾 kir connection + 🔙 encrypted tokens")
    void getValidConnection_ReturnsEncryptedTokens() throws Throwable {

        var count = incomingConnections.count();

        var keyPair = encryptedConnectionsService.generateKeyPair();
        var pubKeyBase64 = encryptedConnectionsService.encodeToBase64(keyPair.getPublic());

        var response = given()
                .body(String.format(VALID_CONNECTION_REQUEST, pubKeyBase64))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var hdKeyBase64 = response.getString("result.hdPublicKey");
        var ivBase64 = response.getString("result.iv");
        var tokensBase64 = response.getString("result.tokens");

        assertThat(ivBase64).isBase64().isNotBlank();
        assertThat(ivBase64).isBase64().isNotBlank();
        assertThat(tokensBase64).isBase64().isNotBlank();

        var tokenJson = decrypt(keyPair.getPrivate(), hdKeyBase64, ivBase64, tokensBase64);

        var tokens = JsonPath.from(tokenJson);

        assertThat(tokens.getString("cat")).is(matching(isCatWith(".proxy.dev.test-gesundheitsamt.de")));
        assertThat(tokens.getString("dat")).is(matching(isUuid()));

        assertThat(incomingConnections.count()).isEqualTo(count + 1);

        verify(proxyClient).announce(announcementCaptor.capture());

        var inTwoHours = Instant.now().plus(Duration.ofHours(2));
        assertThat(announcementCaptor.getValue()).isBeforeOrEqualTo(inTwoHours);
    }

    @Test
    @DisplayName("request connection parameters: invalid Base64 of public key ⇒ 💾 nothing + 🔙 validation error")
    void announceVaccinationInfoList_InvalidBase64PublicKey_ReturnsValidationErrors() {

        var count = incomingConnections.count();

        given()
                .body(String.format(VALID_ANNOUNCEMENT, "x x"))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.data.message", containsString("submitterPublicKey: No valid Base64 encoding"));

        assertThat(incomingConnections.count()).isEqualTo(count);
    }

    @Test
    @DisplayName("submit kir tracing form: invalid dat ⇒ 💾 nothing + 🔙 unauthorized error")
    void submitKirTracingForm_InvalidDat() {

        UUID cat = UUID.randomUUID();

        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());

        incomingConnections.save(incomingKirConnection);

        given()
                .body(String.format(VALID_FORM_SUBMISSION_REQUEST, UUID.randomUUID(), 1, 2, UUID.randomUUID()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("Unknown dataAuthorizationToken:"));

    }

    @Test
    @DisplayName("generate kir access token for form: valid dat ⇒ 💾 kir tracing form + 🔙 access token")
    void generateKirAccessToken_ValidRequest() {

        UUID dat = getConnectionUuid();

        var formsCount = kirTracingForms.count();

        var response = given()
                .body(String.format(VALID_GENERATE_ACCESS_TOKEN_REQUEST, dat))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var accessToken = response.getString("result.accessToken");

        assertThat(accessToken).isNotBlank().hasSize(10);

        KirTracingForm kirTracingForm = kirTracingForms.findByAccessToken(accessToken).get();

        assertEquals(kirTracingForm.getAccessToken(), accessToken);
        assertEquals(kirTracingForm.getTargetDisease(), KirTracingForm.Disease.COVID_19);

        assertEquals(formsCount + 1, kirTracingForms.count());
    }

    @Test
    @DisplayName("submit kir tracing form: valid dat ⇒ 💾 kirtracingform + 🔙 auth token")
    void submitKirTracingForm_ValidRequest() {

        UUID dat = getConnectionUuid();

        var formsCount = kirTracingForms.count();

        final KirTracingForm initialForm = KirTracingForm.builder().build();

        kirTracingForms.save(initialForm);

        final SRP6JavaClientSessionSHA256 client = new SRP6JavaClientSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        final String salt = client
                .generateRandomSalt(SRP6JavascriptServerSessionSHA256.HASH_BYTE_LENGTH);

        final HexHashedVerifierGenerator generator = new HexHashedVerifierGenerator(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10(), SRP6JavascriptServerSessionSHA256.SHA_256);

        final String verifier = generator.generateVerifier(salt, initialForm.getAccessToken(), "Iris$Frankfurt2022!");

        var response = given()
                .body(String.format(VALID_FORM_SUBMISSION_REQUEST, dat, salt, verifier, initialForm.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var accessToken = response.getString("result.accessToken");

        assertThat(accessToken).isNotBlank().hasSize(10);

        KirTracingForm submittedFormInDb = kirTracingForms.findByAccessToken(accessToken).get();

        assertEquals(verifier, submittedFormInDb.getSrpVerifier());
        assertEquals(salt, submittedFormInDb.getSrpSalt());
        assertEquals(initialForm.getAccessToken(), accessToken);
        assertEquals(submittedFormInDb.getTargetDisease(), KirTracingForm.Disease.COVID_19);

        assertEquals(formsCount + 1, kirTracingForms.count());
    }

    @Test
    @DisplayName("challenge kir: valid dat and access token ⇒ 💾 session + 🔙 challenge")
    void challengeKir_ValidRequest() {

        UUID dat = getConnectionUuid();

        var formsCount = kirTracingForms.count();

        final KirTracingForm initialForm = KirTracingForm.builder().build();

        final SRP6JavaClientSessionSHA256 client = new SRP6JavaClientSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        final String salt = client
                .generateRandomSalt(SRP6JavascriptServerSessionSHA256.HASH_BYTE_LENGTH);

        final HexHashedVerifierGenerator generator = new HexHashedVerifierGenerator(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10(), SRP6JavascriptServerSessionSHA256.SHA_256);

        final String verifier = generator.generateVerifier(salt, initialForm.getAccessToken(), "Iris$Frankfurt2022!");

        initialForm.setSrpVerifier(verifier);
        initialForm.setSrpSalt(salt);
        kirTracingForms.save(initialForm);

        var response = given()
                .body(String.format(VALID_CHALLENGE_REQUEST, dat, initialForm.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var challenge = response.getString("result.challenge");

        assertThat(challenge).isNotBlank();

        KirTracingForm submittedFormInDb = kirTracingForms.findByAccessToken(initialForm.getAccessToken()).get();

        assertEquals(verifier, submittedFormInDb.getSrpVerifier());
        assertEquals(salt, submittedFormInDb.getSrpSalt());
        assertEquals(submittedFormInDb.getAccessToken(), initialForm.getAccessToken());
        assertEquals(submittedFormInDb.getTargetDisease(), KirTracingForm.Disease.COVID_19);
        assertNotNull(submittedFormInDb.getSrpSession());
        assertEquals(submittedFormInDb.getSrpSession().getPublicServerValue(), challenge);

        assertEquals(formsCount + 1, kirTracingForms.count());
    }

    @Test
    @DisplayName("update kir tracing form: valid request ⇒ 💾 kirtracingform + 🔙 auth token")
    void updateKirTracingForm_ValidRequest() throws Exception {

        UUID dat = getConnectionUuid();

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()

                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());

        final SRP6JavaClientSessionSHA256 client = new SRP6JavaClientSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        final String salt = client
                .generateRandomSalt(SRP6JavascriptServerSessionSHA256.HASH_BYTE_LENGTH);

        final HexHashedVerifierGenerator generator = new HexHashedVerifierGenerator(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10(), SRP6JavascriptServerSessionSHA256.SHA_256);

        final String verifier = generator.generateVerifier(salt, form.getAccessToken(), "Iris$Frankfurt2022!");

        SRP6JavascriptServerSession srpValidator = new SRP6JavascriptServerSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        String serverChallenge = srpValidator.step1(form.getAccessToken(), salt, verifier);

        form.setSrpSession(srpValidator);
        form.setSrpSalt(salt);
        form.setSrpVerifier(verifier);
        kirTracingForms.save(form);

        var formsCount = kirTracingForms.count();

        client.step1(form.getAccessToken(), "Iris$Frankfurt2022!");

        SRP6ClientCredentials credentials = client.step2(form.getSrpSalt(), serverChallenge);

        var response = given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, credentials.A, credentials.M1, form.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var accessToken = response.getString("result.accessToken");

        KirTracingForm formResult = kirTracingForms.findByAccessToken(accessToken).get();

        assertThat(accessToken).isNotBlank().hasSize(10);
        assertEquals(accessToken, formResult.getAccessToken());
        assertEquals(formsCount, kirTracingForms.count());
        assertEquals(null, formResult.getSrpSession());
        assertEquals("+4915108154711", formResult.getPerson().getMobilePhone());
    }

    @Test
    @DisplayName("update kir tracing form: no srp session ⇒ 💾 nothing + 🔙 error")
    void updateKirTracingForm_NoSrpSession() throws Exception {

        UUID dat = getConnectionUuid();

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()

                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());

        final SRP6JavaClientSessionSHA256 client = new SRP6JavaClientSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        final String salt = client
                .generateRandomSalt(SRP6JavascriptServerSessionSHA256.HASH_BYTE_LENGTH);

        final HexHashedVerifierGenerator generator = new HexHashedVerifierGenerator(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10(), SRP6JavascriptServerSessionSHA256.SHA_256);

        final String verifier = generator.generateVerifier(salt, form.getAccessToken(), "Iris$Frankfurt2022!");

        SRP6JavascriptServerSession srpValidator = new SRP6JavascriptServerSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        String serverChallenge = srpValidator.step1(form.getAccessToken(), salt, verifier);

        form.setSrpSalt(salt);
        form.setSrpVerifier(verifier);
        kirTracingForms.save(form);

        client.step1(form.getAccessToken(), "Iris$Frankfurt2022!");

        SRP6ClientCredentials credentials = client.step2(form.getSrpSalt(), serverChallenge);

        String M2 = srpValidator.step2(toHex(credentials.A), toHex(credentials.M1));

        var response = given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, credentials.A, credentials.M1, form.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("No session"));
    }

    @Test
    @DisplayName("update kir tracing form: invalid password ⇒ 💾 nothing + 🔙 error")
    void updateKirTracingForm_InvalidPassword() throws SRP6Exception {

        UUID dat = getConnectionUuid();

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()

                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());

        final SRP6JavaClientSessionSHA256 client = new SRP6JavaClientSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        final String salt = client
                .generateRandomSalt(SRP6JavascriptServerSessionSHA256.HASH_BYTE_LENGTH);

        final HexHashedVerifierGenerator generator = new HexHashedVerifierGenerator(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10(), SRP6JavascriptServerSessionSHA256.SHA_256);

        final String verifier = generator.generateVerifier(salt, form.getAccessToken(), "Iris$Frankfurt2022!");

        SRP6JavascriptServerSession srpValidator = new SRP6JavascriptServerSessionSHA256(
                srpParamsConfig.getNBase10(), srpParamsConfig.getGBase10());

        String serverChallenge = srpValidator.step1(form.getAccessToken(), salt, verifier);

        form.setSrpSession(srpValidator);
        form.setSrpSalt(salt);
        form.setSrpVerifier(verifier);
        kirTracingForms.save(form);

        var formsCount = kirTracingForms.count();

        client.step1(form.getAccessToken(), "WrongPassword!");

        SRP6ClientCredentials credentials = client.step2(form.getSrpSalt(), serverChallenge);

        var response = given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, credentials.A, credentials.M1, form.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("Invalid credentials"));

    }

    @Test
    @DisplayName("update kir tracing form: invalid auth token ⇒ 💾 nothing + 🔙 error")
    void updateKirTracingForm_InvalidaccessToken() {

        UUID dat = getConnectionUuid();

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()
                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());


        given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, 1, 1, RandomStringUtils.randomAlphanumeric(10).toUpperCase()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("Invalid credentials"));

    }

    private UUID getConnectionUuid() {
        UUID cat = UUID.randomUUID();
        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());
        UUID dat = incomingKirConnection.getId().toUuid();
        incomingConnections.save(incomingKirConnection);
        return dat;
    }

    private String decrypt(PrivateKey privateKey, String hdKeyBase64, String ivBase64, String tokensBase64)
            throws GeneralSecurityException {

        var hdKey = encryptedConnectionsService.decodeFromBase64(hdKeyBase64);
        var aesKey = encryptedConnectionsService.generateAgreedKey(privateKey, hdKey);

        return encryptedConnectionsService.decodeAndDecrypt(aesKey, ivBase64, tokensBase64);
    }
}