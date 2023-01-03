package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.AggregateRepository;

import java.util.Optional;

/**
 * @author Tim Lusa
 */
public interface KirTracingFormRepository extends AggregateRepository<KirTracingForm, KirTracingForm.KirTracingFormIdentifier> {

    Optional<KirTracingForm> findByAccessToken(String accessToken);

}
