package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.config.NodeProperties;
import br.com.api.mice.replicacao.dto.ReplicacaoStatusDTO;
import br.com.api.mice.replicacao.entity.ReplicacaoLogEntity;
import br.com.api.mice.replicacao.service.eleicao.BullyElectionService;
import br.com.api.mice.replicacao.service.replicacao.CidadeReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.EmpresaReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.EstadoReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.FilialReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.PaisReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.UsuarioReplicacaoService;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplicacaoService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final NodeProperties nodeProperties;
    private final BullyElectionService bullyElectionService;
    private final ReplicacaoLogService replicacaoLogService;
    private final PaisReplicacaoService paisReplicacaoService;
    private final EstadoReplicacaoService estadoReplicacaoService;
    private final CidadeReplicacaoService cidadeReplicacaoService;
    private final EmpresaReplicacaoService empresaReplicacaoService;
    private final FilialReplicacaoService filialReplicacaoService;
    private final UsuarioReplicacaoService usuarioReplicacaoService;

    @Transactional
    public ReplicacaoStatusDTO executarReplicacaoCompleta() {
        if (!bullyElectionService.isCurrentNodeLeader()) {
            return ReplicacaoStatusDTO.builder()
                .nodeId(nodeProperties.getId())
                .lider(false)
                .liderAtual(bullyElectionService.getCurrentLeaderId())
                .mensagem("A replicacao so pode ser executada pelo lider atual.")
                .build();
        }

        paisReplicacaoService.replicar();
        estadoReplicacaoService.replicar();
        cidadeReplicacaoService.replicar();
        empresaReplicacaoService.replicar();
        filialReplicacaoService.replicar();
        usuarioReplicacaoService.replicar();

        return obterStatus();
    }

    @Transactional(readOnly = true)
    public ReplicacaoStatusDTO obterStatus() {
        ReplicacaoLogEntity ultimoLog = replicacaoLogService.buscarUltimoLog().orElse(null);
        return ReplicacaoStatusDTO.builder()
            .nodeId(nodeProperties.getId())
            .lider(bullyElectionService.isCurrentNodeLeader())
            .liderAtual(bullyElectionService.getCurrentLeaderId())
            .ultimaExecucaoEm(ultimoLog != null ? ultimoLog.getExecutadoEm().format(FORMATTER) : null)
            .ultimoStatus(ultimoLog != null ? ultimoLog.getStatus() : null)
            .mensagem(ultimoLog != null ? ultimoLog.getMensagem() : "Nenhuma replicacao executada ate o momento.")
            .build();
    }
}
