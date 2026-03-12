package br.com.api.mice.replicacao.scheduler;

import br.com.api.mice.replicacao.service.ReplicacaoService;
import br.com.api.mice.replicacao.service.eleicao.BullyElectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class ReplicacaoScheduler {

    private final BullyElectionService bullyElectionService;
    private final ReplicacaoService replicacaoService;

    @Scheduled(fixedDelayString = "${replicacao.interval-ms}")
    public void executarReplicacao() {
        if (!bullyElectionService.isCurrentNodeLeader()) {
            return;
        }
        log.debug("Node lider executando replicacao agendada.");
        replicacaoService.executarReplicacaoCompleta();
    }
}
