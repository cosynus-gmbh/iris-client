package iris.client_bff.kir_tracing.eps;

import com.fasterxml.jackson.databind.JsonNode;
import iris.client_bff.core.serialization.DefuseJsonString;
import iris.client_bff.core.validation.AttackDetector;
import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.core.validation.NoSignOfAttackJsonNode;
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

    @NoSignOfAttackJsonNode
    private JsonNode assessment;

    @NoSignOfAttackJsonNode
    private JsonNode therapyResults;

    @Data
    @Validated
    public static class PersonDto {

        @NotNull
        @DefuseJsonString(maxLength = 100, payload = AttackDetector.Phone.class)
        private String mobilePhone;

    }
}
