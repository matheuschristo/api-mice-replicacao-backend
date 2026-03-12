package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.client.DjangoApiClient;
import br.com.api.mice.replicacao.dto.FilialDjangoDTO;
import br.com.api.mice.replicacao.entity.CidadeEntity;
import br.com.api.mice.replicacao.entity.EmpresaEntity;
import br.com.api.mice.replicacao.entity.FilialEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.CidadeRepRepository;
import br.com.api.mice.replicacao.repository.EmpresaRepRepository;
import br.com.api.mice.replicacao.repository.FilialRepRepository;
import br.com.api.mice.replicacao.service.ReplicacaoLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilialReplicacaoService {

    private final DjangoApiClient djangoApiClient;
    private final FilialRepRepository filialRepRepository;
    private final EmpresaRepRepository empresaRepRepository;
    private final CidadeRepRepository cidadeRepRepository;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public int replicar() {
        int processados = 0;
        for (FilialDjangoDTO dto : djangoApiClient.buscarFiliais()) {
            EmpresaEntity empresa = empresaRepRepository.findBySourceId(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalStateException("Empresa nao encontrada para sourceId " + dto.getEmpresaId()));
            CidadeEntity cidade = cidadeRepRepository.findBySourceId(dto.getCidadeId())
                .orElseThrow(() -> new IllegalStateException("Cidade nao encontrada para sourceId " + dto.getCidadeId()));

            FilialEntity entity = filialRepRepository.findBySourceId(dto.getId())
                .orElseGet(FilialEntity::new);
            entity.setSourceId(dto.getId());
            entity.setNome(dto.getNome());
            entity.setCodigo(dto.getCodigo());
            entity.setEmpresa(empresa);
            entity.setCidade(cidade);
            entity.setOrigemUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
            entity.setReplicatedAt(LocalDateTime.now());
            filialRepRepository.save(entity);
            processados++;
        }
        replicacaoLogService.registrar("Filial", ReplicacaoStatus.SUCCESS, processados, "Replicacao de filiais concluida.");
        return processados;
    }
}
