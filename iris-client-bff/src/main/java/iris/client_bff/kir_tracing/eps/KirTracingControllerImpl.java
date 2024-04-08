package iris.client_bff.kir_tracing.eps;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6ClientCredentials;
import iris.client_bff.kir_tracing.KirTracingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KirTracingControllerImpl implements KirTracingController {


    private final KirTracingService service;

    @Override
    public KirConnectionResultDto requestKirConnection(KirConnectionDto connectionData) {
        log.debug("Start announcing KIR connection (JSON-RPC interface)");

        var result = service.requestIncomingKirConnectionParameters(connectionData);

        log.trace("Finish announcing KIR connection (JSON-RPC interface)");

        return result;

    }

    @Override
    public KirFormSubmissionResultDto generateKirAccessToken(UUID dataAuthorizationToken) {
        log.debug("Start generating KIR access token (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        var result = service.generateAccessToken();

        log.trace("Finished generating KIR access token (JSON-RPC interface)");

        return result;
    }

    @Override
    public KirChallengeDto challengeKir(UUID dataAuthorizationToken, String accessToken) {
        log.debug("Start KIR challenge (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        var result = service.createChallenge(accessToken);

        log.trace("Finished KIR challenge (JSON-RPC interface)");

        return result;
    }

    @Override
    public KirFormSubmissionResultDto submitKirTracingForm(UUID dataAuthorizationToken, String salt, String verifier, String accessToken, KirTracingFormDto formDto) {
        log.debug("Start submitting KIR form (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        var result = service.submitKirTracingForm(salt, verifier, accessToken, formDto);

        log.trace("Finish submitting KIR form (JSON-RPC interface)");

        return result;

    }

    @Override
    public KirFormSubmissionResultDto submitKirBiohazardExposureAidRequest(UUID dataAuthorizationToken, String a, String m1, String accessToken, JsonNode formDto) {

        log.debug("Start submitting KIR biohazard exposure aid request (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        SRP6ClientCredentials credentials = new SRP6ClientCredentials(BigIntegerUtils.fromHex(a), BigIntegerUtils.fromHex(m1));

        var result = service.updateKirBiohazardExposureAidRequest(credentials, accessToken, formDto);

        log.trace("Finish submitting KIR biohazard exposure aid request (JSON-RPC interface)");

        return result;
    }

    @Override
    public KirFormSubmissionResultDto submitKirMessage(UUID dataAuthorizationToken, String a, String m1, String accessToken, KirTracingFormDto.MessageDto messageDto) {

        log.debug("Start submitting KIR message (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        SRP6ClientCredentials credentials = new SRP6ClientCredentials(BigIntegerUtils.fromHex(a), BigIntegerUtils.fromHex(m1));

        var result = service.submitKirMessage(credentials, accessToken, messageDto);

        log.trace("Finish submitting KIR message (JSON-RPC interface)");

        return result;
    }

    @Override
    public KirFormSubmissionStatusDto getKirFormSubmissionStatus(UUID dataAuthorizationToken, String a, String m1, String accessToken) {

        log.debug("Start getting KIR form submission status (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        SRP6ClientCredentials credentials = new SRP6ClientCredentials(BigIntegerUtils.fromHex(a), BigIntegerUtils.fromHex(m1));

        var result = service.getKirFormSubmissionStatus(credentials, accessToken);

        log.trace("Finish getting KIR form submission status (JSON-RPC interface)");

        return result;
    }

    @Override
    public KirBiohazardEventDto getKirBiohazardEvent() {
        return service.getBiohazardEvent();
    }

    @Override
    public KirAuthorizationResponseDto authorizeKir(UUID dataAuthorizationToken, String a, String m1, String accessToken) {
        log.debug("Start authorizing kir user (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        SRP6ClientCredentials credentials = new SRP6ClientCredentials(BigIntegerUtils.fromHex(a), BigIntegerUtils.fromHex(m1));

        KirAuthorizationResponseDto result = service.authorizeKir(credentials, accessToken);

        log.trace("Finish authorizing kir user (JSON-RPC interface)");

        return result;
    }

    @Override
    public KirFormSubmissionResultDto closeKirSession(UUID dataAuthorizationToken, String a, String m1, String accessToken) {
        log.debug("Start closing KIR session (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        SRP6ClientCredentials credentials = new SRP6ClientCredentials(BigIntegerUtils.fromHex(a), BigIntegerUtils.fromHex(m1));

        var result = service.closeKirSession(credentials, accessToken);

        log.trace("Finished closing KIR session (JSON-RPC interface)");

        return result;
    }

}
