package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.AggregateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * @author Tim Lusa
 */
public interface KirTracingFormRepository extends AggregateRepository<KirTracingForm, KirTracingForm.KirTracingFormIdentifier> {

    Optional<KirTracingForm> findByAccessTokenAndDeletedAtIsNull(String accessToken);
    Page<KirTracingForm> findAllByPersonNotNullAndDeletedAtIsNull(Pageable pageable);
    Page<KirTracingForm> findAllByStatusAndPersonNotNullAndDeletedAtIsNull(KirTracingForm.Status status, Pageable pageable);
    Integer countAllByPersonNullAndDeletedAtIsNull();

}
