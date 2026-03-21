package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.converter.EmpresaConverter;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.CidadeEntity;
import br.com.api.mice.replicacao.entity.EmpresaEntity;
import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.repository.CidadeRepRepository;
import br.com.api.mice.replicacao.repository.EmpresaRepRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaReplicacaoService {

    private final EmpresaRepRepository empresaRepRepository;
    private final CidadeRepRepository cidadeRepRepository;

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
        Long cidadeSourceId = EventoReplicacaoHelper.getLong(data, "cidadeSourceId", "cidadeId", "city");
        CidadeEntity cidade = cidadeRepRepository.findBySourceId(cidadeSourceId)
            .orElseThrow(() -> new IllegalStateException("Cidade nao encontrada para sourceId " + cidadeSourceId));

        EmpresaEntity entity = empresaRepRepository.findBySourceId(sourceId)
            .orElseGet(EmpresaEntity::new);
        entity.setSourceId(sourceId);
        EmpresaConverter.apply(data, entity);
        entity.setCidade(cidade);
        entity.setOrigemUpdatedAt(EventoReplicacaoHelper.updatedAt(evento));
        entity.setReplicatedAt(LocalDateTime.now());
        empresaRepRepository.save(entity);
    }

    private void deletar(EventoReplicacaoDTO evento) {
        empresaRepRepository.findBySourceId(EventoReplicacaoHelper.sourceId(evento))
            .ifPresent(empresaRepRepository::delete);
    }
}
