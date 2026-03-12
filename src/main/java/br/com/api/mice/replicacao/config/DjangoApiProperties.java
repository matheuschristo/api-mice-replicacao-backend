package br.com.api.mice.replicacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "django.api")
public class DjangoApiProperties {

    private String baseUrl;
    private String paisesPath = "/paises";
    private String estadosPath = "/estados";
    private String cidadesPath = "/cidades";
    private String empresasPath = "/empresas";
    private String filiaisPath = "/filiais";
    private String usuariosPath = "/usuarios";
}
