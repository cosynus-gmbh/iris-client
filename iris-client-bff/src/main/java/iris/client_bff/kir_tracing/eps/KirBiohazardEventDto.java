package iris.client_bff.kir_tracing.eps;

import iris.client_bff.core.validation.NoSignOfAttack;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

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

	private Double latitude;

	private Double longitude;

	private Double radius;

	private Double riskFactor;

	private Instant startDate;

	private Instant endDate;
	
	private boolean active;
}
