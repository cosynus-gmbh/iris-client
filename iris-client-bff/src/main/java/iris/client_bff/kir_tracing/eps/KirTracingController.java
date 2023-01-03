package iris.client_bff.kir_tracing.eps;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import iris.client_bff.core.validation.Base64;
import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.core.validation.ValidPassword;
import iris.client_bff.kir_tracing.KirTracingFormDto;
import iris.client_bff.vaccination_info.VaccinationInfoAnnouncementException;
import iris.client_bff.vaccination_info.eps.InvalidPublicKeyException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.UUID;

/**
 * @author Tim Lusa
 */
@Validated
public interface KirTracingController {

	@JsonRpcErrors({
			@JsonRpcError(exception = InvalidPublicKeyException.class, code = -32600),
			@JsonRpcError(exception = VaccinationInfoAnnouncementException.class, code = -32603)
	})
	KirConnectionResultDto requestKirConnection(
			@JsonRpcParam(value = "connectionData") @Valid KirConnectionDto connectionData);

	KirFormSubmissionResultDto submitKirTracingForm(
			@JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
			@JsonRpcParam(value = "password") @NotNull @NoSignOfAttack @ValidPassword String password,
			@JsonRpcParam(value = "form") @NotNull @Valid KirTracingFormDto form
	);

	KirFormSubmissionResultDto updateKirTracingForm(
			@JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
			@JsonRpcParam(value = "password") @NotNull @NoSignOfAttack String password,
			@JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken,
			@JsonRpcParam(value = "form") @NotNull @Valid KirTracingFormDto form
	);

	// DTOs Announcement
	static record KirConnectionDto(
			@NotBlank @Base64 @NoSignOfAttack String submitterPublicKey) {}

	static record KirConnectionResultDto(
			@NotBlank String hdPublicKey,
			@NotBlank String iv,
			@NotBlank String tokens) {}

	static record KirFormSubmissionResultDto(
			@NotBlank String accessToken
	) {}

}
