package iris.client_bff.kir_tracing.eps;

import com.fasterxml.jackson.databind.JsonNode;
import iris.client_bff.core.serialization.DefuseJsonString;
import iris.client_bff.core.validation.AttackDetector;
import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.core.validation.NoSignOfAttackJsonNode;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Validated
@ToString
@Builder
public class KirTracingFormDto {

    @NoSignOfAttack
    private String id;

    @Valid
    private PersonDto person;

    private String status;

    private String targetDisease;

    @NoSignOfAttackJsonNode
    private JsonNode assessment;

    @NoSignOfAttackJsonNode
    private JsonNode therapyResults;

    private Instant createdAt;

    @Data
    @Validated
    public static class PersonDto {

        @NotBlank(message = "api.constraints.person_mobilePhone")
        @DefuseJsonString(maxLength = 100, payload = AttackDetector.Phone.class)
        private String mobilePhone;

    }
}
