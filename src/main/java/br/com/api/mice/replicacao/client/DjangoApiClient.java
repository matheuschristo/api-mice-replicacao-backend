package br.com.api.mice.replicacao.client;

import br.com.api.mice.replicacao.config.DjangoApiProperties;
import br.com.api.mice.replicacao.dto.CidadeDjangoDTO;
import br.com.api.mice.replicacao.dto.EmpresaDjangoDTO;
import br.com.api.mice.replicacao.dto.EstadoDjangoDTO;
import br.com.api.mice.replicacao.dto.FilialDjangoDTO;
import br.com.api.mice.replicacao.dto.PaisDjangoDTO;
import br.com.api.mice.replicacao.dto.UsuarioDjangoDTO;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class DjangoApiClient {

    private final DjangoApiProperties properties;
    private final RestClient djangoRestClient;

    public List<PaisDjangoDTO> buscarPaises() {
        return getList(properties.getPaisesPath(), new ParameterizedTypeReference<>() {
        });
    }

    public List<EstadoDjangoDTO> buscarEstados() {
        return getList(properties.getEstadosPath(), new ParameterizedTypeReference<>() {
        });
    }

    public List<CidadeDjangoDTO> buscarCidades() {
        return getList(properties.getCidadesPath(), new ParameterizedTypeReference<>() {
        });
    }

    public List<EmpresaDjangoDTO> buscarEmpresas() {
        return getList(properties.getEmpresasPath(), new ParameterizedTypeReference<>() {
        });
    }

    public List<FilialDjangoDTO> buscarFiliais() {
        return getList(properties.getFiliaisPath(), new ParameterizedTypeReference<>() {
        });
    }

    public List<UsuarioDjangoDTO> buscarUsuarios() {
        return getList(properties.getUsuariosPath(), new ParameterizedTypeReference<>() {
        });
    }

    private <T> List<T> getList(String path, ParameterizedTypeReference<List<T>> responseType) {
        try {
            List<T> response = djangoRestClient.get()
                .uri(path)
                .retrieve()
                .body(responseType);
            return response == null ? Collections.emptyList() : response;
        } catch (Exception exception) {
            log.warn("Falha ao consultar endpoint Django {}{}: {}", properties.getBaseUrl(), path, exception.getMessage());
            return Collections.emptyList();
        }
    }
}
