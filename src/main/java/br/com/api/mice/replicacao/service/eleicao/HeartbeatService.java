package br.com.api.mice.replicacao.service.eleicao;

import br.com.api.mice.replicacao.config.HeartbeatProperties;
import br.com.api.mice.replicacao.config.NodeProperties;
import br.com.api.mice.replicacao.dto.HeartbeatMessageDTO;
import br.com.api.mice.replicacao.entity.NodeEntity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import br.com.api.mice.replicacao.entity.enums.NodeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartbeatService {

    private final NodeProperties nodeProperties;
    private final HeartbeatProperties heartbeatProperties;
    private final NodeRegistryService nodeRegistryService;
    private final BullyElectionService bullyElectionService;
    private final org.springframework.web.client.RestClient.Builder restClientBuilder;

    public void enviarHeartbeatDoLider() {
        if (!bullyElectionService.isCurrentNodeLeader()) {
            return;
        }

        nodeRegistryService.atualizarHeartbeat(nodeProperties.getId());
        HeartbeatMessageDTO heartbeat = HeartbeatMessageDTO.builder()
            .nodeId(nodeProperties.getId())
            .timestamp(LocalDateTime.now())
            .build();

        for (NodeEntity node : nodeRegistryService.listNosDisponiveisByStatus(List.of(NodeStatus.ACTIVE, NodeStatus.SUSPECT))) {
            if (node.getNodeId().equals(nodeProperties.getId())) {
                continue;   
            }
            try {
                restClientBuilder.baseUrl("http://" + node.getHost() + ":" + node.getPorta())
                    .build()
                    .post()
                    .uri("/nodes/heartbeat")
                    .body(heartbeat)
                    .retrieve()
                    .toBodilessEntity();
            } catch (Exception exception) {
                log.warn("Falha ao enviar heartbeat para node {}: {}", node.getNodeId(), exception.getMessage());
                nodeRegistryService.marcarComoSuspeito(node.getNodeId());
            }
        }
    }

    public void processarHeartbeat(HeartbeatMessageDTO heartbeatMessageDTO) {
        nodeRegistryService.atualizarHeartbeat(heartbeatMessageDTO.getNodeId());
    }

    public void verificarTimeoutDoLider() {
        if (bullyElectionService.isCurrentNodeLeader()) {
            return;
        }

        NodeEntity lider = nodeRegistryService.obterLiderAtual();
        if (lider == null || lider.getLastHeartbeatAt() == null) {
            bullyElectionService.iniciarEleicao();
            return;
        }

        long elapsedMs = Duration.between(lider.getLastHeartbeatAt(), LocalDateTime.now()).toMillis();
        if (elapsedMs > heartbeatProperties.getTimeoutMs()) {
            nodeRegistryService.marcarComoSuspeito(lider.getNodeId());
            bullyElectionService.limparLiderAtual();
            bullyElectionService.iniciarEleicao();
        }
    }
}
