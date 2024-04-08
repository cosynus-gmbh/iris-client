package iris.client_bff.kir_tracing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class KirBiohazardEventInitializer {

	@Qualifier("KirBiohazardEventProperties")
	private final KirBiohazardEventProperties properties;

	private final KirBioHazardEventRepository repository;

	@PostConstruct
	protected void createOrUpdateBiohazardEvent() {
		Optional<KirBiohazardEvent> event = repository.findFirstByActiveIsTrue();
		KirBiohazardEvent biohazardEvent = event.orElseGet(KirBiohazardEvent::new);
		biohazardEvent
				.setRadius(properties.getRadius())
				.setLatitude(properties.getLatitude())
				.setLongitude(properties.getLongitude())
				.setSubstance(KirBiohazardEvent.Substance.TOXIC_EXAMPLE)
				.setActive(true);
		repository.save(biohazardEvent);
	}
}
