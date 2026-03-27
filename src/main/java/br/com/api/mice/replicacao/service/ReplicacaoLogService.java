package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.config.NodeProperties;
import br.com.api.mice.replicacao.entity.ReplicacaoLogEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.ReplicacaoLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplicacaoLogService {

    private final NodeProperties nodeProperties;
    private final ReplicacaoLogRepository replicacaoLogRepository;

    @Transactional
    public void registrar(String entidade, String eventId, ReplicacaoStatus status, int registrosProcessados, String mensagem,
                          LocalDateTime executadoEm, LocalDateTime finalizadoEm) {
        replicacaoLogRepository.save(ReplicacaoLogEntity.builder()
            .entidade(entidade)
            .eventId(eventId)
            .nodeId(nodeProperties.getId())
            .status(status)
            .registrosProcessados(registrosProcessados)
            .mensagem(mensagem)
            .executadoEm(executadoEm)
            .finalizadoEm(finalizadoEm)
            .build());
    }

    @Transactional(readOnly = true)
    public Optional<ReplicacaoLogEntity> buscarUltimoLog() {
        return replicacaoLogRepository.findTopByOrderByExecutadoEmDesc();
    }

    @Transactional(readOnly = true)
    public List<ReplicacaoLogEntity> buscarUltimosLogs() {
        return replicacaoLogRepository.findTop10ByOrderByExecutadoEmDesc();
    }
}
