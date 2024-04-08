package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.AggregateRepository;

import java.util.Optional;

public interface KirBioHazardEventRepository extends AggregateRepository<KirBiohazardEvent, KirBiohazardEvent.KirBiohazardEventIdentifier> {

    Optional<KirBiohazardEvent> findFirstByActiveIsTrue();
}
