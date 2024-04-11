package iris.client_bff.kir_tracing.eps;

import com.fasterxml.jackson.annotation.JsonProperty;
import iris.client_bff.core.serialization.DefuseJsonString;
import iris.client_bff.core.validation.GeoLocationCoordinate;
import iris.client_bff.core.validation.NoSignOfAttack;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@Validated
@ToString
@Builder
public class KirBiohazardEventDto {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String id;

	@NoSignOfAttack
	@Size(max = 512)
	@DefuseJsonString(maxLength = 512)
	private String substance;

	private Instant startDate;

	private Instant endDate;
	
	private boolean active;

	@Valid
	private LocationDto location;

	@Min(1)
	private Double locationRadius;

	private Instant createdAt;

	private Instant lastModifiedAt;

	@Data
	@Validated
	public static class LocationDto {

		@NoSignOfAttack
		@Size(max = 256)
		@DefuseJsonString(maxLength = 256)
		private String id;

		@NoSignOfAttack
		@Size(max = 32)
		@DefuseJsonString(maxLength = 32)
		private String postcode;

		@NoSignOfAttack
		@Size(max = 512)
		@DefuseJsonString(maxLength = 512)
		private String city;

		@GeoLocationCoordinate
		private Double latitude;

		@GeoLocationCoordinate
		private Double longitude;

	}
}
