package iris.client_bff.kir_tracing;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component("KirBiohazardEventProperties")
@Configuration
@ConfigurationProperties("iris.client.kirtracing.biohazard-event")
@ConstructorBinding
@Validated
@Data
public class KirBiohazardEventProperties {
    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Double radius;
}
