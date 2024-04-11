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
import javax.validation.constraints.Size;
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

    @Size(max = 50)
    @NoSignOfAttack
    @DefuseJsonString(maxLength = 50)
    private String status;

    @NoSignOfAttackJsonNode
    private JsonNode assessment;

    private List<@Valid AidRequestDto> aidRequests;

    private Double riskFactor;

    private List<@Valid MessageDto> messages;

    private Instant createdAt;

    @Data
    @Validated
    public static class PersonDto {

        @Size(max = 100)
        @NotBlank(message = "api.constraints.person_mobilePhone")
        @NoSignOfAttack(payload = AttackDetector.Phone.class)
        @DefuseJsonString(maxLength = 100, payload = AttackDetector.Phone.class)
        private String mobilePhone;

    }

    @Data
    @Validated
    public static class AidRequestDto {

        @NoSignOfAttackJsonNode
        private JsonNode data;

        private Instant createdAt;

    }
    @Data
    @Validated
    public static class MessageDto {

        @Size(max = 6000)
        @NotBlank(message = "api.constraints.message")
        @NoSignOfAttack
        @DefuseJsonString(maxLength = 6000)
        private String text;

        private Instant createdAt;

    }
}
