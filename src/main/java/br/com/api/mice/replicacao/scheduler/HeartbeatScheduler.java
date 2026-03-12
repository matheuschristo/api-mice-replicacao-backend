package br.com.api.mice.replicacao.scheduler;

import br.com.api.mice.replicacao.service.eleicao.BullyElectionService;
import br.com.api.mice.replicacao.service.eleicao.HeartbeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class HeartbeatScheduler {

    private final BullyElectionService bullyElectionService;
    private final HeartbeatService heartbeatService;

    @Scheduled(fixedDelayString = "${heartbeat.interval-ms}")
    public void executarHeartbeat() {
        if (bullyElectionService.isCurrentNodeLeader()) {
            heartbeatService.enviarHeartbeatDoLider();
            return;
        }
        heartbeatService.verificarTimeoutDoLider();
    }
}
