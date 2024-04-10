package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.*;
import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, of = {})
@ToString
@Indexed
@Slf4j
@NoArgsConstructor
public class KirBiohazardEvent extends Aggregate<KirBiohazardEvent, KirBiohazardEvent.KirBiohazardEventIdentifier> {

	{
		id = KirBiohazardEventIdentifier.of(UUID.randomUUID());
	}

	@ToString.Exclude
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<KirTracingForm> tracingForms = new ArrayList<>();

	private String substance = null;

	@Column
	private Instant startDate = null;

	@Column
	private Instant endDate = null;

	private boolean active = false;

	@IndexedEmbedded
	private Location location = new Location();

	// radius in meters
	@Column(columnDefinition = "DECIMAL(25,5)", nullable = false)
	private Double locationRadius;

	@EqualsAndHashCode(callSuper = false)
	@RequiredArgsConstructor(staticName = "of")
	@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE) // for JPA
	public static class KirBiohazardEventIdentifier extends IdWithUuid {

		@Serial
		private static final long serialVersionUID = 4831742437513922343L;

		final UUID id;

		static KirBiohazardEventIdentifier random() {
			return of(UUID.randomUUID());
		}

		@Override
		protected UUID getBasicId() {
			return id;
		}
	}

	@Embeddable
	@Data
	@Setter(AccessLevel.PACKAGE)
	@Builder
	@AllArgsConstructor(staticName = "of")
	@RequiredArgsConstructor
	public static class Location {

		@Column(name = "location_id")
		private String id;

		@Column(name = "location_postcode")
		private String postcode;

		@Column(name = "location_city")
		private String city;

		@Column(name = "location_latitude", columnDefinition = "DECIMAL(30,20)", nullable = false)
		private Double latitude;

		@Column(name = "location_longitude", columnDefinition = "DECIMAL(30,20)", nullable = false)
		private Double longitude;

	}
}
