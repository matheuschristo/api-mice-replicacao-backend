package br.com.api.mice.replicacao.service.eleicao;

import br.com.api.mice.replicacao.config.HeartbeatProperties;
import br.com.api.mice.replicacao.config.NodeProperties;
import br.com.api.mice.replicacao.dto.NodeInfoDTO;
import br.com.api.mice.replicacao.entity.NodeEntity;
import br.com.api.mice.replicacao.entity.enums.NodeStatus;
import br.com.api.mice.replicacao.repository.NodeRepository;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NodeRegistryService {

    private static final long INACTIVE_TIMEOUT_MULTIPLIER = 3L;

    private final NodeRepository nodeRepository;
    private final NodeProperties nodeProperties;
    private final HeartbeatProperties heartbeatProperties;

    @PostConstruct
    @Transactional
    public void registrarNoAtual() {
        NodeEntity node = nodeRepository.findByNodeId(nodeProperties.getId())
            .orElseGet(() -> NodeEntity.builder()
                .nodeId(nodeProperties.getId())
                .build());

        node.setHost(nodeProperties.getHost());
        node.setPorta(nodeProperties.getPorta());
        node.setLeader(Boolean.FALSE);
        node.setStatus(NodeStatus.ACTIVE);
        node.setLastHeartbeatAt(LocalDateTime.now());
        nodeRepository.save(node);
    }

    @Transactional
    public NodeEntity atualizarHeartbeat(String nodeId) {
        NodeEntity node = nodeRepository.findByNodeId(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node nao encontrado: " + nodeId));
        node.setStatus(NodeStatus.ACTIVE);
        node.setLastHeartbeatAt(LocalDateTime.now());
        return nodeRepository.save(node);
    }

    @Transactional
    public List<NodeEntity> listNosDisponiveisByStatus(List<NodeStatus> status) {
        return nodeRepository.findByStatusIn(status);
    }

    @Transactional(readOnly = true)
    public List<NodeEntity> listarTodos() {
        return nodeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public NodeEntity obterNoAtual() {
        return nodeRepository.findByNodeId(nodeProperties.getId())
            .orElseThrow(() -> new IllegalStateException("Node atual nao foi registrado."));
    }

    @Transactional(readOnly = true)
    public NodeEntity obterLiderAtual() {
        return nodeRepository.findByLeaderTrue().orElse(null);
    }

    @Transactional
    public void atualizarLider(String leaderId) {
        List<NodeEntity> nodes = nodeRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (NodeEntity node : nodes) {
            boolean isLeader = node.getNodeId().equals(leaderId);
            node.setLeader(isLeader);
            if (isLeader) {
                node.setStatus(NodeStatus.ACTIVE);
                node.setLastHeartbeatAt(now);
            }
        }
        nodeRepository.saveAll(nodes);
    }

    @Transactional
    public void marcarComoSuspeito(String nodeId) {
        nodeRepository.findByNodeId(nodeId).ifPresent(node -> {
            node.setLeader(Boolean.FALSE);
            if (node.getStatus() == NodeStatus.INACTIVE) {
                nodeRepository.save(node);
                return;
            }
            LocalDateTime heartbeat = node.getLastHeartbeatAt();
            long elapsedMs = heartbeat == null
                ? Long.MAX_VALUE
                : Duration.between(heartbeat, LocalDateTime.now()).toMillis();
            long inactiveThresholdMs = heartbeatProperties.getTimeoutMs() * INACTIVE_TIMEOUT_MULTIPLIER;
            node.setStatus(elapsedMs >= inactiveThresholdMs ? NodeStatus.INACTIVE : NodeStatus.SUSPECT);
            nodeRepository.save(node);
        });
    }

    public NodeInfoDTO toDto(NodeEntity entity) {
        if (entity == null) {
            return null;
        }
        return NodeInfoDTO.builder()
            .nodeId(entity.getNodeId())
            .host(entity.getHost())
            .porta(entity.getPorta())
            .leader(entity.getLeader())
            .status(entity.getStatus())
            .lastHeartbeatAt(entity.getLastHeartbeatAt() != null ? entity.getLastHeartbeatAt().toString() : null)
            .build();
    }
}
