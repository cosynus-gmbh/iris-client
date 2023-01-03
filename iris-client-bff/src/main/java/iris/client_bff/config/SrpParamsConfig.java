package iris.client_bff.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "srp")
@ConstructorBinding
@Validated
@Data
public class SrpParamsConfig {

    @NotNull
    private String nBase10;

    @NotNull
    private String gBase10;

}
