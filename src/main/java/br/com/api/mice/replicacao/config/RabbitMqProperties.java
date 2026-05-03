package br.com.api.mice.replicacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "replicacao.rabbitmq")
public class RabbitMqProperties {

    private String exchange = "hcm.replicacao";
    private String queue = "hcm.replicacao.eventos";
    private String deadLetterExchange = "hcm.replicacao.dlx";
    private String deadLetterQueue = "hcm.replicacao.eventos.dlq";
    private String routingKey = "replicacao.#";
}
