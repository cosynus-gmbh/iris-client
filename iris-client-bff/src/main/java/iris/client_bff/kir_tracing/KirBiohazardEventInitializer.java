package iris.client_bff.kir_tracing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
@Slf4j
public class KirBiohazardEventInitializer {

	@Qualifier("KirBiohazardEventProperties")
	private final KirBiohazardEventProperties properties;

	private final KirBioHazardEventRepository repository;

	@PostConstruct
	protected void createBiohazardEventIfNotExist() {
		if (repository.count() == 0) {
			KirBiohazardEvent biohazardEvent = new KirBiohazardEvent();
			KirBiohazardEvent.Location location = biohazardEvent.getLocation()
					.setLatitude(properties.getLatitude())
					.setLongitude(properties.getLongitude());
			biohazardEvent
					.setLocation(location)
					.setLocationRadius(properties.getRadius())
					.setActive(true);
			repository.save(biohazardEvent);
		} else {
			log.info("Initial kir biohazard event already exists.");
		}
	}
}
