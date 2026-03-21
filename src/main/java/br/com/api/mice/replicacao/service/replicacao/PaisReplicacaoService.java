package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.converter.PaisConverter;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.PaisEntity;
import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.repository.PaisRepRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaisReplicacaoService {

    private final PaisRepRepository paisRepRepository;

    @Transactional
    public void processarEvento(EventoReplicacaoDTO evento) {
        TipoAcaoReplicacao acao = evento.getAction();
        switch (acao) {
            case CREATE, UPDATE -> salvarOuAtualizar(evento);
            case DELETE -> deletar(evento);
        }
    }

    private void salvarOuAtualizar(EventoReplicacaoDTO evento) {
        Long sourceId = EventoReplicacaoHelper.sourceId(evento);
        Map<String, Object> data = evento.getData();

        PaisEntity entity = paisRepRepository.findBySourceId(sourceId)
            .orElseGet(PaisEntity::new);
        entity.setSourceId(sourceId);
        PaisConverter.apply(data, entity);
        entity.setOrigemUpdatedAt(EventoReplicacaoHelper.updatedAt(evento));
        entity.setReplicatedAt(LocalDateTime.now());
        paisRepRepository.save(entity);
    }

    private void deletar(EventoReplicacaoDTO evento) {
        paisRepRepository.findBySourceId(EventoReplicacaoHelper.sourceId(evento))
            .ifPresent(paisRepRepository::delete);
    }
}
