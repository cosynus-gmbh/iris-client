package iris.client_bff.kir_tracing;

import iris.client_bff.core.serialization.DefuseJsonString;
import iris.client_bff.core.validation.AttackDetector;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class KirTracingFormDto {

    @Valid
    private PersonDto person;

    private String status;

    private String targetDisease;

    @Data
    @Validated
    public static class PersonDto {

        @NotNull
        @DefuseJsonString(maxLength = 100, payload = AttackDetector.Phone.class)
        private String mobilePhone;

    }
}
