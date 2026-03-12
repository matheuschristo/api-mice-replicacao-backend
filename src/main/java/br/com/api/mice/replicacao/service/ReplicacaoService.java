package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.config.NodeProperties;
import br.com.api.mice.replicacao.dto.ReplicacaoStatusDTO;
import br.com.api.mice.replicacao.entity.ReplicacaoLogEntity;
import br.com.api.mice.replicacao.service.eleicao.BullyElectionService;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @Transactional(readOnly = true)
    public ReplicacaoStatusDTO obterStatus() {
        ReplicacaoLogEntity ultimoLog = replicacaoLogService.buscarUltimoLog().orElse(null);
        return ReplicacaoStatusDTO.builder()
            .nodeId(nodeProperties.getId())
            .lider(bullyElectionService.isCurrentNodeLeader())
            .liderAtual(bullyElectionService.getCurrentLeaderId())
            .ultimaExecucaoEm(ultimoLog != null ? ultimoLog.getExecutadoEm().format(FORMATTER) : null)
            .ultimoStatus(ultimoLog != null ? ultimoLog.getStatus() : null)
            .mensagem(ultimoLog != null ? ultimoLog.getMensagem() : "Nenhum evento de replicacao processado ate o momento.")
            .build();
    }

    @Transactional(readOnly = true)
    public List<ReplicacaoLogEntity> buscarUltimosLogs() {
        return replicacaoLogService.buscarUltimosLogs();
    }
}
