package iris.client_bff.config;

import com.nimbusds.srp6.SRP6CryptoParams;
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

    public SRP6CryptoParams getConfig() {
      return SRP6CryptoParams.getInstance(2048, "SHA-256");
    }

    public int getSaltByteCount() {
        return 32;
    }

    private Integer sessionTimeout;

    public int getSessionTimeout() {
        if (this.sessionTimeout != null) {
            return this.sessionTimeout;
        }
        return 0;
    }
}
