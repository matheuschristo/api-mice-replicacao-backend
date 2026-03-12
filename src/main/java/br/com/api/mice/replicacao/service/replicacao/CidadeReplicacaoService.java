package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.client.DjangoApiClient;
import br.com.api.mice.replicacao.dto.CidadeDjangoDTO;
import br.com.api.mice.replicacao.entity.CidadeEntity;
import br.com.api.mice.replicacao.entity.EstadoEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.CidadeRepRepository;
import br.com.api.mice.replicacao.repository.EstadoRepRepository;
import br.com.api.mice.replicacao.service.ReplicacaoLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CidadeReplicacaoService {

    private final DjangoApiClient djangoApiClient;
    private final CidadeRepRepository cidadeRepRepository;
    private final EstadoRepRepository estadoRepRepository;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public int replicar() {
        int processados = 0;
        for (CidadeDjangoDTO dto : djangoApiClient.buscarCidades()) {
            EstadoEntity estado = estadoRepRepository.findBySourceId(dto.getEstadoId())
                .orElseThrow(() -> new IllegalStateException("Estado nao encontrado para sourceId " + dto.getEstadoId()));

            CidadeEntity entity = cidadeRepRepository.findBySourceId(dto.getId())
                .orElseGet(CidadeEntity::new);
            entity.setSourceId(dto.getId());
            entity.setNome(dto.getNome());
            entity.setCodigoIbge(dto.getCodigoIbge());
            entity.setEstado(estado);
            entity.setOrigemUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
            entity.setReplicatedAt(LocalDateTime.now());
            cidadeRepRepository.save(entity);
            processados++;
        }
        replicacaoLogService.registrar("Cidade", ReplicacaoStatus.SUCCESS, processados, "Replicacao de cidades concluida.");
        return processados;
    }
}
