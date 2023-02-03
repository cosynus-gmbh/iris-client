package iris.client_bff.kir_tracing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import iris.client_bff.IrisWebIntegrationTest;
import iris.client_bff.WithMockIrisUser;
import iris.client_bff.kir_tracing.KirTracingForm;
import iris.client_bff.kir_tracing.KirTracingFormRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@IrisWebIntegrationTest
@WithMockIrisUser
@RequiredArgsConstructor
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

        String assessmentJson = """
                {"form":{"infected":true}}
                """;


        KirTracingForm form = KirTracingForm.builder()
                .srpSalt("TestpassUnencrypted")
                .srpVerifier("Testverifier")
                .accessToken(RandomStringUtils.randomAlphanumeric(10)
                        .toUpperCase())
                .assessment(assessmentJson.trim())
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
                .get(path, "infected")
                .then()
                .body("numberOfElements", is(1), getCommonChecks())
                .extract();

        System.out.println(result.body()
                .asString());
    }

    @Test
    @DisplayName("updateKirTracingFormStatus: valid request => 💾 status ⇒ 🔙 form")
    void updateTracingFormStatus_Success() throws Exception {

        KirTracingForm form = KirTracingForm.builder()
                .srpSalt("TestpassUnencrypted")
                .srpVerifier("Testverifier")
                .accessToken(RandomStringUtils.randomAlphanumeric(10)
                        .toUpperCase())
                .assessment("""
                        {"form":{"infected":true}}
                        """.trim())
                .person(KirTracingForm.Person.builder()
                        .mobilePhone("+4915141800093")
                        .build())
                .build();

        kirTracingForms.save(form);

        var path = BASE_URL + "/" + form.getId();

        KirTracingFormStatusUpdateDto patch = KirTracingFormStatusUpdateDto.builder()
                .status(KirTracingForm.Status.DONE)
                .build();

        ObjectMapper om = new ObjectMapper();

        var patchBody = om.writeValueAsString(patch);

        mvc
                .perform(
                        MockMvcRequestBuilders.patch(path)
                                .content(patchBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andReturn();

        KirTracingForm updatedForm = kirTracingForms.findById(form.getId())
                .get();

        assertEquals(KirTracingForm.Status.DONE, updatedForm.getStatus());
    }

    private Object[] getCommonChecks() {

        return new Object[]{
                "pageable.offset", is(0)};
    }
}