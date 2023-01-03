package iris.client_bff.kir_tracing;

import iris.client_bff.vaccination_info.VaccinationInfoAnnouncement;
import iris.client_bff.vaccination_info.VaccinationInfoAnnouncement.AnnouncementIdentifier;
import iris.client_bff.vaccination_info.VaccinationInfoAnnouncementRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * This class collects all old vaccination info announcements and deletes this.
 *
 * @author Tim Lusa
 */
@Component
@RequiredArgsConstructor
@Slf4j
class IncomingKirConnectionDeleteJob {

	private final @NonNull IncomingKirConnectionRepository connections;
	private final @NonNull Properties properties;

	@Transactional
	@Scheduled(cron = "${iris.client.vaccinfo.announcement.delete-cron:-}")
	void deleteAnnouncements() {

		var refDate = Instant.now().minus(properties.getDeleteAfter());

		var oldAnnouncements = connections.findByMetadataCreatedIsBefore(refDate).toList();

		if (oldAnnouncements.isEmpty()) {
			return;
		}

		log.debug("{} kir tracing incoming connection announcement(s) are deleted with duration {} after their creation!",
				oldAnnouncements.size(),
				properties.getDeleteAfter(),
				oldAnnouncements.get(0).getCreatedAt());

		connections.deleteAll(oldAnnouncements);

		log.info("{} kir tracing incoming connection announcement(s) (IDs: {}) were deleted with duration {} after their creation at {}!",
				oldAnnouncements.size(),
				oldAnnouncements.stream()
						.map(IncomingKirConnection::getId)
						.map(IncomingKirConnection.IncomingKirConnectionIdentifier::toString)
						.collect(Collectors.joining(", ")),
				properties.getDeleteAfter(),
				oldAnnouncements.get(0).getCreatedAt());
	}

	@ConfigurationProperties("iris.client.kir-tracing.announcement")
	@ConstructorBinding
	@Value
	static class Properties {

		/**
		 * Defines the {@link Duration} after that a vaccination info announcement will be deleted starting from the
		 * creation date.
		 */
		private final Duration deleteAfter;
	}
}
