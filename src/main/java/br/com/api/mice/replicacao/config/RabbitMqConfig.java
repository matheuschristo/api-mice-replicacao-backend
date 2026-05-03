package br.com.api.mice.replicacao.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

    private final RabbitMqProperties rabbitMqProperties;

    @Bean
    public TopicExchange replicacaoExchange() {
        return new TopicExchange(rabbitMqProperties.getExchange(), true, false);
    }

    @Bean
    public TopicExchange replicacaoDeadLetterExchange() {
        return new TopicExchange(rabbitMqProperties.getDeadLetterExchange(), true, false);
    }

    @Bean
    public Queue replicacaoQueue() {
        return new Queue(
            rabbitMqProperties.getQueue(),
            true,
            false,
            false,
            Map.of("x-dead-letter-exchange", rabbitMqProperties.getDeadLetterExchange())
        );
    }

    @Bean
    public Queue replicacaoDeadLetterQueue() {
        return new Queue(rabbitMqProperties.getDeadLetterQueue(), true);
    }

    @Bean
    public Binding replicacaoBinding() {
        return BindingBuilder.bind(replicacaoQueue())
            .to(replicacaoExchange())
            .with(rabbitMqProperties.getRoutingKey());
    }

    @Bean
    public Binding replicacaoDeadLetterBinding() {
        return BindingBuilder.bind(replicacaoDeadLetterQueue())
            .to(replicacaoDeadLetterExchange())
            .with("#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
