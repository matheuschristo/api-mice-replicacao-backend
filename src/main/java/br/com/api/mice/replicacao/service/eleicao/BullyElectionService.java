package br.com.api.mice.replicacao.service.eleicao;

import br.com.api.mice.replicacao.config.ElectionProperties;
import br.com.api.mice.replicacao.config.NodeProperties;
import br.com.api.mice.replicacao.dto.CoordinatorMessageDTO;
import br.com.api.mice.replicacao.dto.ElectionMessageDTO;
import br.com.api.mice.replicacao.entity.NodeEntity;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import br.com.api.mice.replicacao.entity.enums.NodeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class BullyElectionService {

    private final NodeProperties nodeProperties;
    private final ElectionProperties electionProperties;
    private final NodeRegistryService nodeRegistryService;
    private final RestClient.Builder restClientBuilder;

    private final AtomicReference<String> currentLeaderId = new AtomicReference<>();

    public synchronized String iniciarEleicao() {
        currentLeaderId.set(null);
        NodeEntity noAtual = nodeRegistryService.obterNoAtual();
        List<NodeEntity> nosSuperiores = nodeRegistryService.listNosDisponiveisByStatus(List.of(NodeStatus.ACTIVE, NodeStatus.SUSPECT))
            .stream()
                .filter(node -> !node.getNodeId().equals(noAtual.getNodeId()))
                .filter(node -> compareNodeId(node.getNodeId(), noAtual.getNodeId()) > 0)
                .sorted(Comparator.comparing(NodeEntity::getNodeId))
                .toList();

        boolean algumNoSuperiorRespondeu = false;
        for (NodeEntity noSuperior : nosSuperiores) {
            algumNoSuperiorRespondeu = enviarMensagemEleicao(noSuperior) || algumNoSuperiorRespondeu;
        }

        if (!algumNoSuperiorRespondeu) {
            definirCoordenador(CoordinatorMessageDTO.builder()
                .leaderId(noAtual.getNodeId())
                .host(noAtual.getHost())
                .porta(noAtual.getPorta())
                .build());
            notificarNovoCoordenador();
            return noAtual.getNodeId();
        }

        log.info("Eleicao iniciada pelo node {}. Aguardando coordenador por ate {} ms.", noAtual.getNodeId(), electionProperties.getTimeoutMs());
        return currentLeaderId.get();
    }

    public void responderEleicao(ElectionMessageDTO electionMessageDTO) {
        NodeEntity noAtual = nodeRegistryService.obterNoAtual();
        if (compareNodeId(noAtual.getNodeId(), electionMessageDTO.getNodeId()) <= 0) {
            return;
        }

        notificarOk(electionMessageDTO);
        CompletableFuture.runAsync(this::iniciarEleicao);
    }

    public void registrarRespostaEleicao(ElectionMessageDTO electionMessageDTO) {
        currentLeaderId.compareAndSet(null, electionMessageDTO.getNodeId());
    }

    public synchronized void definirCoordenador(CoordinatorMessageDTO coordinatorMessageDTO) {
        currentLeaderId.set(coordinatorMessageDTO.getLeaderId());
        nodeRegistryService.atualizarLider(coordinatorMessageDTO.getLeaderId());
    }

    public void limparLiderAtual() {
        currentLeaderId.set(null);
    }

    public boolean isCurrentNodeLeader() {
        return nodeProperties.getId().equals(getCurrentLeaderId());
    }

    public String getCurrentLeaderId() {
        String leaderId = currentLeaderId.get();
        if (leaderId != null) {
            return leaderId;
        }
        NodeEntity lider = nodeRegistryService.obterLiderAtual();
        if (lider != null) {
            currentLeaderId.set(lider.getNodeId());
            return lider.getNodeId();
        }
        return null;
    }

    private boolean enviarMensagemEleicao(NodeEntity destino) {
        try {
            buildClient(destino).post()
                .uri("/election/start")
                .body(ElectionMessageDTO.builder()
                    .nodeId(nodeProperties.getId())
                    .host(nodeProperties.getHost())
                    .porta(nodeProperties.getPorta())
                    .build())
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (Exception exception) {
            log.warn("Nao foi possivel contactar node {} durante a eleicao: {}", destino.getNodeId(), exception.getMessage());
            return false;
        }
    }

    private void notificarOk(ElectionMessageDTO destino) {
        try {
            restClientBuilder.baseUrl(buildBaseUrl(destino.getHost(), destino.getPorta()))
                .build()
                .post()
                .uri("/election/ok")
                .body(ElectionMessageDTO.builder()
                    .nodeId(nodeProperties.getId())
                    .host(nodeProperties.getHost())
                    .porta(nodeProperties.getPorta())
                    .build())
                .retrieve()
                .toBodilessEntity();
        } catch (Exception exception) {
            log.warn("Falha ao enviar OK de eleicao para node {}: {}", destino.getNodeId(), exception.getMessage());
        }
    }

    private void notificarNovoCoordenador() {
        CoordinatorMessageDTO coordinator = CoordinatorMessageDTO.builder()
            .leaderId(nodeProperties.getId())
            .host(nodeProperties.getHost())
            .porta(nodeProperties.getPorta())
            .build();

        for (NodeEntity node : nodeRegistryService.listNosDisponiveisByStatus(List.of(NodeStatus.ACTIVE, NodeStatus.SUSPECT))) {
            if (node.getNodeId().equals(nodeProperties.getId())) {
                continue;
            }
            try {
                buildClient(node).post()
                    .uri("/election/coordinator")
                    .body(coordinator)
                    .retrieve()
                    .toBodilessEntity();
            } catch (Exception exception) {
                log.warn("Falha ao notificar coordenador para node {}: {}", node.getNodeId(), exception.getMessage());
            }
        }
    }

    private RestClient buildClient(NodeEntity node) {
        return restClientBuilder.baseUrl(buildBaseUrl(node.getHost(), node.getPorta())).build();
    }

    private String buildBaseUrl(String host, Integer porta) {
        return "http://" + host + ":" + porta;
    }

    private int compareNodeId(String left, String right) {
        try {
            return Long.compare(Long.parseLong(left), Long.parseLong(right));
        } catch (NumberFormatException exception) {
            return left.compareTo(right);
        }
    }
}
