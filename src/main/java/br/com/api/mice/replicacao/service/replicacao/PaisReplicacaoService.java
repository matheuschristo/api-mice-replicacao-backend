package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.client.DjangoApiClient;
import br.com.api.mice.replicacao.dto.PaisDjangoDTO;
import br.com.api.mice.replicacao.entity.PaisEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.PaisRepRepository;
import br.com.api.mice.replicacao.service.ReplicacaoLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaisReplicacaoService {

    private final DjangoApiClient djangoApiClient;
    private final PaisRepRepository paisRepRepository;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public int replicar() {
        int processados = 0;
        for (PaisDjangoDTO dto : djangoApiClient.buscarPaises()) {
            PaisEntity entity = paisRepRepository.findBySourceId(dto.getId())
                .orElseGet(PaisEntity::new);
            entity.setSourceId(dto.getId());
            entity.setNome(dto.getNome());
            entity.setSigla(dto.getSigla());
            entity.setOrigemUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
            entity.setReplicatedAt(LocalDateTime.now());
            paisRepRepository.save(entity);
            processados++;
        }
        replicacaoLogService.registrar("Pais", ReplicacaoStatus.SUCCESS, processados, "Replicacao de paises concluida.");
        return processados;
    }
}
