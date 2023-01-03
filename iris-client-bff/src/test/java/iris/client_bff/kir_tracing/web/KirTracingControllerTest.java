package iris.client_bff.kir_tracing.web;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import iris.client_bff.IrisWebIntegrationTest;
import iris.client_bff.WithMockIrisUser;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingFormRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.HttpStatus.OK;

@IrisWebIntegrationTest
@WithMockIrisUser
// removes saved entities before this test so that other tests not affected this one
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("kir-tracing")
@Tag("rest-controller")
@DisplayName("IT of web controller for kir tracing forms")
class KirTracingControllerTest {

    private static final String BASE_URL = "/kir-tracing";

    final MockMvc mvc;
    final KirTracingFormRepository kirTracingForms;

    @BeforeEach
    void setUp() {

        Locale.setDefault(Locale.ENGLISH);

        RestAssuredMockMvc.mockMvc(mvc);
    }

    @Test
    @DisplayName("getKirTracingForms: without search string ⇒ 🔙 all results")
    void getKirTracingForms_AllResults() {

        KirTracingForm form = KirTracingForm.builder()
                .srpSalt("TestpassUnencrypted")
                .srpVerifier("Testverifier")
                .accessToken(RandomStringUtils.randomAlphanumeric(10)
                        .toUpperCase())
                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915141800093")
                        .build())
                .build();

        kirTracingForms.save(form);

        var path = BASE_URL + "?search={search}";

        when()
                .get(path, "For SearchingAB")
                .then()
                .status(OK);

        var result = when()
                .get(path, "TestpassUnencrypted")
                .then()
                .body("numberOfElements", is(1), getCommonChecks())
                .extract()
                .jsonPath();

        System.out.println(result.get()
                .toString());
    }

    private Object[] getCommonChecks() {

        return new Object[]{
                "pageable.offset", is(0)};
    }
}