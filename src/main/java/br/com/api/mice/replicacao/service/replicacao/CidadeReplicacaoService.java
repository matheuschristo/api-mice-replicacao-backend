package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.converter.CidadeConverter;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.CidadeEntity;
import br.com.api.mice.replicacao.entity.EstadoEntity;
import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.repository.CidadeRepRepository;
import br.com.api.mice.replicacao.repository.EstadoRepRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CidadeReplicacaoService {

    private final CidadeRepRepository cidadeRepRepository;
    private final EstadoRepRepository estadoRepRepository;

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
        Long estadoSourceId = EventoReplicacaoHelper.getLong(data, "estadoSourceId", "estadoId", "state");
        EstadoEntity estado = estadoRepRepository.findBySourceId(estadoSourceId)
            .orElseThrow(() -> new IllegalStateException("Estado nao encontrado para sourceId " + estadoSourceId));

        CidadeEntity entity = cidadeRepRepository.findBySourceId(sourceId)
            .orElseGet(CidadeEntity::new);
        entity.setSourceId(sourceId);
        CidadeConverter.apply(data, entity);
        entity.setEstado(estado);
        entity.setOrigemUpdatedAt(EventoReplicacaoHelper.updatedAt(evento));
        entity.setReplicatedAt(LocalDateTime.now());
        cidadeRepRepository.save(entity);
    }

    private void deletar(EventoReplicacaoDTO evento) {
        cidadeRepRepository.findBySourceId(EventoReplicacaoHelper.sourceId(evento))
            .ifPresent(cidadeRepRepository::delete);
    }
}
