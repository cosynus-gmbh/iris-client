package iris.client_bff.kir_tracing.eps;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class KirChallengeDto {
    private String challenge;
    private String salt;
}
