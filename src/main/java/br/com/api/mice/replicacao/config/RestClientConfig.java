package br.com.api.mice.replicacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient djangoRestClient(RestClient.Builder builder, DjangoApiProperties properties) {
        return builder.baseUrl(properties.getBaseUrl()).build();
    }
}
