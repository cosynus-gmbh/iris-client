package iris.client_bff.kir_tracing.eps;

import iris.client_bff.kir_tracing.KirTracingFormDto;
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
    public KirFormSubmissionResultDto submitKirTracingForm(UUID dataAuthorizationToken, String password, KirTracingFormDto formDto) {
        log.debug("Start submitting KIR form (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        var result = service.submitKirTracingForm(password, formDto);

        log.trace("Finish submitting KIR form (JSON-RPC interface)");

        return result;

    }

    @Override
    public KirFormSubmissionResultDto updateKirTracingForm(UUID dataAuthorizationToken, String password, String accessToken, KirTracingFormDto formDto) {

        log.debug("Start updating KIR form (JSON-RPC interface)");

        if (!service.validateConnection(dataAuthorizationToken)) {
            throw new IllegalArgumentException("Unknown dataAuthorizationToken: " + dataAuthorizationToken);
        }

        var result = service.updateKirTracingForm(password, accessToken, formDto);

        log.trace("Finish updating KIR form (JSON-RPC interface)");

        return result;
    }
}
