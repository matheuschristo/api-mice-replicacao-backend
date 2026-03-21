package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.converter.EstadoConverter;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.EstadoEntity;
import br.com.api.mice.replicacao.entity.PaisEntity;
import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.repository.EstadoRepRepository;
import br.com.api.mice.replicacao.repository.PaisRepRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstadoReplicacaoService {

    private final EstadoRepRepository estadoRepRepository;
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
        Long paisSourceId = EventoReplicacaoHelper.getLong(data, "paisSourceId", "paisId", "country");
        PaisEntity pais = paisRepRepository.findBySourceId(paisSourceId)
            .orElseThrow(() -> new IllegalStateException("Pais nao encontrado para sourceId " + paisSourceId));

        EstadoEntity entity = estadoRepRepository.findBySourceId(sourceId)
            .orElseGet(EstadoEntity::new);
        entity.setSourceId(sourceId);
        EstadoConverter.apply(data, entity);
        entity.setPais(pais);
        entity.setOrigemUpdatedAt(EventoReplicacaoHelper.updatedAt(evento));
        entity.setReplicatedAt(LocalDateTime.now());
        estadoRepRepository.save(entity);
    }

    private void deletar(EventoReplicacaoDTO evento) {
        estadoRepRepository.findBySourceId(EventoReplicacaoHelper.sourceId(evento))
            .ifPresent(estadoRepRepository::delete);
    }
}
