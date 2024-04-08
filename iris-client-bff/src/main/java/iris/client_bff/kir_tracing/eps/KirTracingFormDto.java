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
import java.util.List;

@Data
@Validated
@ToString
@Builder
public class KirTracingFormDto {

    @NoSignOfAttack
    private String id;

    @Valid
    private PersonDto person;

    @NoSignOfAttack
    private String status;

    private Double riskFactor;

    private List<@Valid MessageDto> messages;

    @NoSignOfAttackJsonNode
    private JsonNode assessment;

    @NoSignOfAttackJsonNode
    private JsonNode aidRequest;

    private Instant createdAt;

    @Data
    @Validated
    public static class PersonDto {

        @NotBlank(message = "api.constraints.person_mobilePhone")
        @NoSignOfAttack(payload = AttackDetector.Phone.class)
        @DefuseJsonString(maxLength = 100, payload = AttackDetector.Phone.class)
        private String mobilePhone;

    }

    @Data
    @Validated
    public static class MessageDto {

        @NotBlank(message = "api.constraints.message")
        @NoSignOfAttack()
        @DefuseJsonString(maxLength = 6000)
        private String text;

        private Instant createdAt;

    }
}
