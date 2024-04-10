package iris.client_bff.kir_tracing.eps;

import iris.client_bff.core.validation.NoSignOfAttack;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;

@Data
@Validated
@ToString
@Builder
public class KirBiohazardEventDto {

	@NoSignOfAttack
	private String id;

	@NoSignOfAttack
	private String substance;

	private Instant startDate;

	private Instant endDate;
	
	private boolean active;

	@Valid
	private LocationDto location;

	private Double locationRadius;

	@Data
	@Validated
	public static class LocationDto {

		@NoSignOfAttack
		private String id;

		@NoSignOfAttack
		private String postcode;

		@NoSignOfAttack
		private String city;

		private Double latitude;

		private Double longitude;

	}
}
