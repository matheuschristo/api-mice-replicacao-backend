package br.com.api.mice.replicacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "replicacao")
public class ReplicacaoProperties {

    private Long intervalMs = 60000L;
}
