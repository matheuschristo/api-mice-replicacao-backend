package br.com.api.mice.replicacao.controller;

import br.com.api.mice.replicacao.config.WebhookProperties;
import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.service.EventoReplicacaoService;
import br.com.api.mice.replicacao.service.eleicao.BullyElectionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/replicacao")
@RequiredArgsConstructor
public class WebhookReplicacaoController {

    private static final String TOKEN_HEADER = "X-Webhook-Token";

    private final WebhookProperties webhookProperties;
    private final BullyElectionService bullyElectionService;
    private final EventoReplicacaoService eventoReplicacaoService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> receberEvento(
        @RequestBody EventoReplicacaoDTO eventoReplicacaoDTO,
        @RequestHeader(value = TOKEN_HEADER, required = false) String token
    ) {
        if (!Boolean.TRUE.equals(webhookProperties.getEnabled())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("status", "webhook_disabled"));
        }

        if (webhookProperties.getSecurityToken() != null
            && !webhookProperties.getSecurityToken().isBlank()
            && !webhookProperties.getSecurityToken().equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", "invalid_token"));
        }

        if (!bullyElectionService.isCurrentNodeLeader()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "status", "not_leader",
                    "leaderId", bullyElectionService.getCurrentLeaderId()
                ));
        }

        try {
            eventoReplicacaoService.processarEvento(eventoReplicacaoDTO);
            return ResponseEntity.accepted()
                .body(Map.of(
                    "status", "processed",
                    "eventId", eventoReplicacaoDTO.getEventId()
                ));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "status", "error",
                    "message", exception.getMessage()
                ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "enabled", webhookProperties.getEnabled(),
            "leader", bullyElectionService.isCurrentNodeLeader(),
            "leaderId", bullyElectionService.getCurrentLeaderId()
        ));
    }
}
