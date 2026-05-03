package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMqReplicacaoConsumer {

    private final EventoReplicacaoService eventoReplicacaoService;

    @RabbitListener(queues = "${replicacao.rabbitmq.queue}")
    public void consumir(EventoReplicacaoDTO evento) {
        log.info("Evento recebido do RabbitMQ | eventId={} entity={} action={}",
            evento.getEventId(), evento.getEntity(), evento.getAction());
        eventoReplicacaoService.processarEvento(evento);
    }
}
