package iris.client_bff.kir_tracing.web;

import iris.client_bff.kir_tracing.KirTracingForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KirTracingFormStatusUpdateDto {

    @NotNull
    private KirTracingForm.Status status;

}
