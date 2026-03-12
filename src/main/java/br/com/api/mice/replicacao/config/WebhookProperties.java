package br.com.api.mice.replicacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "webhook")
public class WebhookProperties {

    private Boolean enabled = Boolean.TRUE;
    private String securityToken;
}
