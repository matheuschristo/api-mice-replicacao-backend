package br.com.api.mice.replicacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "election")
public class ElectionProperties {

    private Long timeoutMs = 5000L;
}
