package iris.client_bff.kir_tracing;

import iris.client_bff.core.model.Aggregate;
import iris.client_bff.core.model.IdWithUuid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.persistence.*;
import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@GenericField(sortable = Sortable.YES)
	private Substance substance = Substance.TOXIC_EXAMPLE;

	@Column(columnDefinition = "DECIMAL(20,10)", nullable = false)
	private Double latitude;

	@Column(columnDefinition = "DECIMAL(20,10)", nullable = false)
	private Double longitude;

	// radius in meters
	@Column(columnDefinition = "DECIMAL(25,5)", nullable = false)
	private Double radius;

	@Column
	private Instant startDate = null;

	@Column
	private Instant endDate = null;

	private boolean active = false;

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

	public enum Substance {
		TOXIC_EXAMPLE
	}
}
