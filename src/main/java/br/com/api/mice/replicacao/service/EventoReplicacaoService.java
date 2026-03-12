package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventoReplicacaoService {

    private final ReplicacaoDispatcherService replicacaoDispatcherService;
    private final ReplicacaoLogService replicacaoLogService;

    @Transactional
    public void processarEvento(EventoReplicacaoDTO evento) {
        validarEvento(evento);
        LocalDateTime inicio = LocalDateTime.now();
        try {
            replicacaoDispatcherService.dispatch(evento);
            replicacaoLogService.registrar(
                evento.getEntity().name(),
                evento.getEventId(),
                ReplicacaoStatus.SUCCESS,
                1,
                "Evento " + evento.getAction() + " processado com sucesso.",
                inicio,
                LocalDateTime.now()
            );
        } catch (RuntimeException exception) {
            replicacaoLogService.registrar(
                evento.getEntity().name(),
                evento.getEventId(),
                ReplicacaoStatus.ERROR,
                0,
                exception.getMessage(),
                inicio,
                LocalDateTime.now()
            );
            throw exception;
        }
    }

    private void validarEvento(EventoReplicacaoDTO evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Payload do evento nao informado.");
        }
        if (evento.getEventId() == null || evento.getEventId().isBlank()) {
            throw new IllegalArgumentException("Campo eventId e obrigatorio.");
        }
        if (evento.getEntity() == null) {
            throw new IllegalArgumentException("Campo entity e obrigatorio.");
        }
        if (evento.getAction() == null) {
            throw new IllegalArgumentException("Campo action e obrigatorio.");
        }
        if (evento.getSourceId() == null || evento.getSourceId().isBlank()) {
            throw new IllegalArgumentException("Campo sourceId e obrigatorio.");
        }
    }
}
