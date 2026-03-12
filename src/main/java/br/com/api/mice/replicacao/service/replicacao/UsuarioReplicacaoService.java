package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.client.DjangoApiClient;
import br.com.api.mice.replicacao.dto.UsuarioDjangoDTO;
import br.com.api.mice.replicacao.entity.EmpresaEntity;
import br.com.api.mice.replicacao.entity.FilialEntity;
import br.com.api.mice.replicacao.entity.UsuarioEntity;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import br.com.api.mice.replicacao.repository.EmpresaRepRepository;
import br.com.api.mice.replicacao.repository.FilialRepRepository;
import br.com.api.mice.replicacao.repository.UsuarioRepRepository;
import br.com.api.mice.replicacao.service.ReplicacaoLogService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioReplicacaoService {

    private final DjangoApiClient djangoApiClient;
    private final UsuarioRepRepository usuarioRepRepository;
    private final EmpresaRepRepository empresaRepRepository;
    private final FilialRepRepository filialRepRepository;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public int replicar() {
        int processados = 0;
        for (UsuarioDjangoDTO dto : djangoApiClient.buscarUsuarios()) {
            EmpresaEntity empresa = empresaRepRepository.findBySourceId(dto.getEmpresaId())
                .orElseThrow(() -> new IllegalStateException("Empresa nao encontrada para sourceId " + dto.getEmpresaId()));
            FilialEntity filial = filialRepRepository.findBySourceId(dto.getFilialId())
                .orElseThrow(() -> new IllegalStateException("Filial nao encontrada para sourceId " + dto.getFilialId()));

            UsuarioEntity entity = usuarioRepRepository.findBySourceId(dto.getId())
                .orElseGet(UsuarioEntity::new);
            entity.setSourceId(dto.getId());
            entity.setNome(dto.getNome());
            entity.setEmail(dto.getEmail());
            entity.setAtivo(Boolean.TRUE.equals(dto.getAtivo()));
            entity.setEmpresa(empresa);
            entity.setFilial(filial);
            entity.setOrigemUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
            entity.setReplicatedAt(LocalDateTime.now());
            usuarioRepRepository.save(entity);
            processados++;
        }
        replicacaoLogService.registrar("Usuario", ReplicacaoStatus.SUCCESS, processados, "Replicacao de usuarios concluida.");
        return processados;
    }
}
