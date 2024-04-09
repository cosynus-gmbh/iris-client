package iris.client_bff.kir_tracing.eps;

import com.fasterxml.jackson.databind.JsonNode;
import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import iris.client_bff.core.validation.Base64;
import iris.client_bff.core.validation.NoSignOfAttack;
import iris.client_bff.core.validation.NoSignOfAttackJsonNode;
import iris.client_bff.kir_tracing.KirTracingException;
import iris.client_bff.vaccination_info.eps.InvalidPublicKeyException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Tim Lusa
 */
@Validated
public interface KirTracingController {

    @JsonRpcErrors({
            @JsonRpcError(exception = InvalidPublicKeyException.class, code = -32600),
            @JsonRpcError(exception = KirTracingException.class, code = -32604)
    })
    KirConnectionResultDto requestKirConnection(
            @JsonRpcParam(value = "connectionData") @Valid KirConnectionDto connectionData);

    KirFormSubmissionResultDto generateKirAccessToken(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken
    );

    KirChallengeDto challengeKir(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken
    );

    KirFormSubmissionResultDto closeKirSession(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "a") @NotNull String a,
            @JsonRpcParam(value = "m1") @NotNull String m1,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken
    );

    KirFormSubmissionResultDto submitKirTracingForm(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "salt") @NotNull @NoSignOfAttack String salt,
            @JsonRpcParam(value = "verifier") @NotNull @NoSignOfAttack String verifier,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken,
            @JsonRpcParam(value = "form") @NotNull @Valid KirTracingFormDto form
    );

    KirAuthorizationResponseDto authorizeKir(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "a") @NotNull String a,
            @JsonRpcParam(value = "m1") @NotNull String m1,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken
    );

    KirFormSubmissionResultDto submitKirBiohazardExposureAidRequest(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "a") @NotNull String a,
            @JsonRpcParam(value = "m1") @NotNull String m1,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken,
            @JsonRpcParam(value = "form") @NotNull @Valid KirTracingFormDto.AidRequestDto aidRequestDto
    );

    KirFormSubmissionResultDto submitKirMessage(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "a") @NotNull String a,
            @JsonRpcParam(value = "m1") @NotNull String m1,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken,
            @JsonRpcParam(value = "message") @NotNull @Valid KirTracingFormDto.MessageDto messageDto
    );

    KirFormSubmissionStatusDto getKirFormSubmissionStatus(
            @JsonRpcParam(value = "dataAuthorizationToken") @NotNull UUID dataAuthorizationToken,
            @JsonRpcParam(value = "a") @NotNull String a,
            @JsonRpcParam(value = "m1") @NotNull String m1,
            @JsonRpcParam(value = "accessToken") @NotNull @NoSignOfAttack String accessToken
    );

    KirBiohazardEventDto getKirBiohazardEvent();

    // DTOs Announcement
    record KirConnectionDto(
            @NotBlank @Base64 @NoSignOfAttack String submitterPublicKey
    ) {}

    record KirConnectionResultDto(
            @NotBlank String hdPublicKey,
            @NotBlank String iv,
            @NotBlank String tokens
    ) {}

    record KirFormSubmissionResultDto(
            @NotBlank String accessToken
    ) {}

    record KirAuthorizationResponseDto(
            @NotBlank String M2
    ) {}

    record KirFormSubmissionStatusDto(
            @NotBlank String createdAt,
            @NotNull Boolean hasAssessment,
            @NotNull Boolean hasAidRequest
    ) {}

}
