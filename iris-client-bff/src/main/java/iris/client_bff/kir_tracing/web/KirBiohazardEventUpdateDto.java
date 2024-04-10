package iris.client_bff.kir_tracing.web;

import iris.client_bff.core.validation.NoSignOfAttack;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@Validated
@ToString
@Builder
public class KirBiohazardEventUpdateDto {

	@Nullable
	@NoSignOfAttack
	@Size(max = 512)
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
		@Size(max = 256)
		private String id;

		@NoSignOfAttack
		@Size(max = 32)
		private String postcode;

		@NoSignOfAttack
		@Size(max = 512)
		private String city;

		private Double latitude;

		private Double longitude;

	}
}
