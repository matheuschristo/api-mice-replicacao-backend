package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.converter.UsuarioConverter;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.EmpresaEntity;
import br.com.api.mice.replicacao.entity.FilialEntity;
import br.com.api.mice.replicacao.entity.UsuarioEntity;
import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.repository.EmpresaRepRepository;
import br.com.api.mice.replicacao.repository.FilialRepRepository;
import br.com.api.mice.replicacao.repository.UsuarioRepRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioReplicacaoService {

    private final UsuarioRepRepository usuarioRepRepository;
    private final EmpresaRepRepository empresaRepRepository;
    private final FilialRepRepository filialRepRepository;

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
        Long filialSourceId = EventoReplicacaoHelper.getLong(data, "filialSourceId", "filialId", "branch");
        EmpresaEntity empresa = empresaRepRepository.findBySourceId(empresaSourceId)
            .orElseThrow(() -> new IllegalStateException("Empresa nao encontrada para sourceId " + empresaSourceId));
        FilialEntity filial = filialRepRepository.findBySourceId(filialSourceId)
            .orElseThrow(() -> new IllegalStateException("Filial nao encontrada para sourceId " + filialSourceId));

        UsuarioEntity entity = usuarioRepRepository.findBySourceId(sourceId)
            .orElseGet(UsuarioEntity::new);
        entity.setSourceId(sourceId);
        UsuarioConverter.apply(data, entity);
        entity.setEmpresa(empresa);
        entity.setFilial(filial);
        entity.setOrigemUpdatedAt(EventoReplicacaoHelper.updatedAt(evento));
        entity.setReplicatedAt(LocalDateTime.now());
        usuarioRepRepository.save(entity);
    }

    private void deletar(EventoReplicacaoDTO evento) {
        usuarioRepRepository.findBySourceId(EventoReplicacaoHelper.sourceId(evento))
            .ifPresent(usuarioRepRepository::delete);
    }
}
