package br.com.api.mice.replicacao.entity;

import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "replicacao_log")
public class ReplicacaoLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entidade;

    @Column(name = "event_id", length = 100)
    private String eventId;

    @Column(name = "node_id", nullable = false, length = 100)
    private String nodeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReplicacaoStatus status;

    @Column(name = "registros_processados", nullable = false)
    private Integer registrosProcessados;

    @Column(length = 1000)
    private String mensagem;

    @Column(name = "executado_em", nullable = false)
    private LocalDateTime executadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;
}
