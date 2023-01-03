package iris.client_bff.kir_tracing.eps;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import iris.client_bff.IrisWebIntegrationTest;
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

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static iris.client_bff.kir_tracing.eps.KirTracingTestData.*;
import static iris.client_bff.matchers.IrisMatchers.isCatWith;
import static iris.client_bff.matchers.IrisMatchers.isUuid;
import static iris.client_bff.vaccination_info.eps.VaccinationInfoAnnouncmentTestData.VALID_ANNOUNCEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.HamcrestCondition.matching;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@IrisWebIntegrationTest
@RequiredArgsConstructor
@Tag("kir-tracing")
@Tag("json-rpc-controller")
@DisplayName("IT of JSON-RPC controller for kir tracing")
class KirTracingControllerIntegrationTest {

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

        System.out.println(response.toString());

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
                .body(String.format(VALID_FORM_SUBMISSION_REQUEST, UUID.randomUUID(), "Iris$Frankfurt2022!"))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("Unknown dataAuthorizationToken:"));

    }

    @Test
    @DisplayName("submit kir tracing form: valid dat ⇒ 💾 kirtracingform + 🔙 auth token")
    void submitKirTracingForm_ValidRequest() {

        UUID cat = UUID.randomUUID();
        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());
        UUID dat = incomingKirConnection.getId().toUuid();
        incomingConnections.save(incomingKirConnection);

        var formsCount = kirTracingForms.count();

        var response = given()
                .body(String.format(VALID_FORM_SUBMISSION_REQUEST, dat, "Iris$Frankfurt2022!"))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var accessToken = response.getString("result.accessToken");

        assertThat(accessToken).isNotBlank().hasSize(10);

        KirTracingForm kirTracingForm = kirTracingForms.findByAccessToken(accessToken).get();

        assertTrue(passwordEncoder.matches("Iris$Frankfurt2022!", kirTracingForm.getPassword()));
        assertEquals(kirTracingForm.getAccessToken(), accessToken);
        assertEquals(kirTracingForm.getTargetDisease(), KirTracingForm.Disease.COVID_19);

        assertEquals(formsCount + 1, kirTracingForms.count());
    }

    @Test
    @DisplayName("update kir tracing form: valid request ⇒ 💾 kirtracingform + 🔙 auth token")
    void updateKirTracingForm_ValidRequest() {

        UUID cat = UUID.randomUUID();
        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());
        UUID dat = incomingKirConnection.getId().toUuid();
        incomingConnections.save(incomingKirConnection);

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()
                        .password(passwordEncoder.encode("Iris$Frankfurt2022!"))
                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());

        var formsCount = kirTracingForms.count();

        var response = given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, "Iris$Frankfurt2022!", form.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(OK)
                .extract()
                .jsonPath();

        var accessToken = response.getString("result.accessToken");

        assertThat(accessToken).isNotBlank().hasSize(10);

        assertTrue(passwordEncoder.matches("Iris$Frankfurt2022!", form.getPassword()));
        assertEquals(form.getAccessToken(), accessToken);

        assertEquals(formsCount, kirTracingForms.count());
    }

    @Test
    @DisplayName("update kir tracing form: invalid password ⇒ 💾 nothing + 🔙 error")
    void updateKirTracingForm_InvalidPassword() {

        UUID cat = UUID.randomUUID();
        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());
        UUID dat = incomingKirConnection.getId().toUuid();
        incomingConnections.save(incomingKirConnection);

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()
                .password(passwordEncoder.encode("Iris$Frankfurt2022!"))
                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());

        var formsCount = kirTracingForms.count();

        var response = given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, "WrongPassword!", form.getAccessToken()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("Invalid credentials"));

    }

    @Test
    @DisplayName("update kir tracing form: invalid auth token ⇒ 💾 nothing + 🔙 error")
    void updateKirTracingForm_InvalidaccessToken() {

        UUID cat = UUID.randomUUID();
        IncomingKirConnection incomingKirConnection = IncomingKirConnection.of(cat.toString());
        UUID dat = incomingKirConnection.getId().toUuid();
        incomingConnections.save(incomingKirConnection);

        KirTracingForm form = kirTracingForms.save(KirTracingForm.builder()
                .password(passwordEncoder.encode("Iris$Frankfurt2022!"))
                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915147110815")
                        .build())
                .build());

        var formsCount = kirTracingForms.count();

        var response = given()
                .body(String.format(VALID_FORM_UPDATE_REQUEST, dat, "Iris$Frankfurt2022!", RandomStringUtils.randomAlphanumeric(10).toUpperCase()))

                .when()
                .post("/data-submission-rpc")

                .then()
                .status(BAD_REQUEST)
                .body("error.message", containsString("Invalid credentials"));

    }

    private String decrypt(PrivateKey privateKey, String hdKeyBase64, String ivBase64, String tokensBase64)
            throws GeneralSecurityException {

        var hdKey = encryptedConnectionsService.decodeFromBase64(hdKeyBase64);
        var aesKey = encryptedConnectionsService.generateAgreedKey(privateKey, hdKey);

        return encryptedConnectionsService.decodeAndDecrypt(aesKey, ivBase64, tokensBase64);
    }
}