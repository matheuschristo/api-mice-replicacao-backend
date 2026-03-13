package br.com.api.mice.replicacao.dto;

import br.com.api.mice.replicacao.entity.enums.TipoAcaoReplicacao;
import br.com.api.mice.replicacao.entity.enums.TipoEntidadeReplicacao;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoReplicacaoDTO {

    private String eventId;
    private TipoEntidadeReplicacao entity;
    private TipoAcaoReplicacao action;
    private String sourceId;
    private LocalDateTime updatedAt;
    private Map<String, Object> data;
}
