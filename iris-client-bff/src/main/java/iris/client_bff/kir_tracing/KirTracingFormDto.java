package iris.client_bff.kir_tracing;

import com.fasterxml.jackson.databind.JsonNode;
import iris.client_bff.core.serialization.DefuseJsonString;
import iris.client_bff.core.validation.AttackDetector;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Validated
@ToString
public class KirTracingFormDto {

    @Valid
    private PersonDto person;

    private String status;

    private String targetDisease;

    private JsonNode assessment;

    private String therapyResults;

    @Data
    @Validated
    public static class PersonDto {

        @NotNull
        @DefuseJsonString(maxLength = 100, payload = AttackDetector.Phone.class)
        private String mobilePhone;

    }
}
