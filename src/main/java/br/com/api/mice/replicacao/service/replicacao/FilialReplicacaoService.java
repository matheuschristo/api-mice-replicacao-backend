package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.converter.FilialConverter;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.CidadeEntity;
import br.com.api.mice.replicacao.entity.EmpresaEntity;
import br.com.api.mice.replicacao.entity.FilialEntity;
import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.repository.CidadeRepRepository;
import br.com.api.mice.replicacao.repository.EmpresaRepRepository;
import br.com.api.mice.replicacao.repository.FilialRepRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilialReplicacaoService {

    private final FilialRepRepository filialRepRepository;
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
        Long empresaSourceId = EventoReplicacaoHelper.getLong(data, "empresaSourceId", "empresaId", "company");
        Long cidadeSourceId = EventoReplicacaoHelper.getLong(data, "cidadeSourceId", "cidadeId", "city");
        EmpresaEntity empresa = empresaRepRepository.findBySourceId(empresaSourceId)
            .orElseThrow(() -> new IllegalStateException("Empresa nao encontrada para sourceId " + empresaSourceId));
        CidadeEntity cidade = cidadeRepRepository.findBySourceId(cidadeSourceId)
            .orElseThrow(() -> new IllegalStateException("Cidade nao encontrada para sourceId " + cidadeSourceId));

        FilialEntity entity = filialRepRepository.findBySourceId(sourceId)
            .orElseGet(FilialEntity::new);
        entity.setSourceId(sourceId);
        FilialConverter.apply(data, entity);
        entity.setEmpresa(empresa);
        entity.setCidade(cidade);
        entity.setOrigemUpdatedAt(EventoReplicacaoHelper.updatedAt(evento));
        entity.setReplicatedAt(LocalDateTime.now());
        filialRepRepository.save(entity);
    }

    private void deletar(EventoReplicacaoDTO evento) {
        filialRepRepository.findBySourceId(EventoReplicacaoHelper.sourceId(evento))
            .ifPresent(filialRepRepository::delete);
    }
}
