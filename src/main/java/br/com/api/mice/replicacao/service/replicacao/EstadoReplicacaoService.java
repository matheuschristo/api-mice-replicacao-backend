package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.client.DjangoApiClient;
import br.com.api.mice.replicacao.dto.EstadoDjangoDTO;
import br.com.api.mice.replicacao.entity.EstadoEntity;
import br.com.api.mice.replicacao.entity.PaisEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.EstadoRepRepository;
import br.com.api.mice.replicacao.repository.PaisRepRepository;
import br.com.api.mice.replicacao.service.ReplicacaoLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstadoReplicacaoService {

    private final DjangoApiClient djangoApiClient;
    private final EstadoRepRepository estadoRepRepository;
    private final PaisRepRepository paisRepRepository;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public int replicar() {
        int processados = 0;
        for (EstadoDjangoDTO dto : djangoApiClient.buscarEstados()) {
            PaisEntity pais = paisRepRepository.findBySourceId(dto.getPaisId())
                .orElseThrow(() -> new IllegalStateException("Pais nao encontrado para sourceId " + dto.getPaisId()));

            EstadoEntity entity = estadoRepRepository.findBySourceId(dto.getId())
                .orElseGet(EstadoEntity::new);
            entity.setSourceId(dto.getId());
            entity.setNome(dto.getNome());
            entity.setSigla(dto.getSigla());
            entity.setPais(pais);
            entity.setOrigemUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
            entity.setReplicatedAt(LocalDateTime.now());
            estadoRepRepository.save(entity);
            processados++;
        }
        replicacaoLogService.registrar("Estado", ReplicacaoStatus.SUCCESS, processados, "Replicacao de estados concluida.");
        return processados;
    }
}
