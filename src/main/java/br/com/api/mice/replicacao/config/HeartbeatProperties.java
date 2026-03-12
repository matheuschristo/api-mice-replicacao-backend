package br.com.api.mice.replicacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "heartbeat")
public class HeartbeatProperties {

    private Long intervalMs = 5000L;
    private Long timeoutMs = 15000L;
}
