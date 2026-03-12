package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.entity.ReplicacaoLogEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.ReplicacaoLogRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplicacaoLogService {

    private final ReplicacaoLogRepository replicacaoLogRepository;

    @Transactional
    public void registrar(String entidade, ReplicacaoStatus status, int registrosProcessados, String mensagem) {
        replicacaoLogRepository.save(ReplicacaoLogEntity.builder()
            .entidade(entidade)
            .status(status)
            .registrosProcessados(registrosProcessados)
            .mensagem(mensagem)
            .executadoEm(LocalDateTime.now())
            .build());
    }

    @Transactional(readOnly = true)
    public Optional<ReplicacaoLogEntity> buscarUltimoLog() {
        return replicacaoLogRepository.findTopByOrderByExecutadoEmDesc();
    }
}
