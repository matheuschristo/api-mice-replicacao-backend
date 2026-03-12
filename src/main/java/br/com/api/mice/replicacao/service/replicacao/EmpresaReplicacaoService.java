package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.client.DjangoApiClient;
import br.com.api.mice.replicacao.dto.EmpresaDjangoDTO;
import br.com.api.mice.replicacao.entity.CidadeEntity;
import br.com.api.mice.replicacao.entity.EmpresaEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.CidadeRepRepository;
import br.com.api.mice.replicacao.repository.EmpresaRepRepository;
import br.com.api.mice.replicacao.service.ReplicacaoLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaReplicacaoService {

    private final DjangoApiClient djangoApiClient;
    private final EmpresaRepRepository empresaRepRepository;
    private final CidadeRepRepository cidadeRepRepository;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public int replicar() {
        int processados = 0;
        for (EmpresaDjangoDTO dto : djangoApiClient.buscarEmpresas()) {
            CidadeEntity cidade = cidadeRepRepository.findBySourceId(dto.getCidadeId())
                .orElseThrow(() -> new IllegalStateException("Cidade nao encontrada para sourceId " + dto.getCidadeId()));

            EmpresaEntity entity = empresaRepRepository.findBySourceId(dto.getId())
                .orElseGet(EmpresaEntity::new);
            entity.setSourceId(dto.getId());
            entity.setRazaoSocial(dto.getRazaoSocial());
            entity.setNomeFantasia(dto.getNomeFantasia());
            entity.setCnpj(dto.getCnpj());
            entity.setCidade(cidade);
            entity.setOrigemUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
            entity.setReplicatedAt(LocalDateTime.now());
            empresaRepRepository.save(entity);
            processados++;
        }
        replicacaoLogService.registrar("Empresa", ReplicacaoStatus.SUCCESS, processados, "Replicacao de empresas concluida.");
        return processados;
    }
}
