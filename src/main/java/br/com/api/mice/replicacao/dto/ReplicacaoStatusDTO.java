package br.com.api.mice.replicacao.dto;

import br.com.api.mice.replicacao.entity.enums.ReplicacaoStatus;
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
public class ReplicacaoStatusDTO {

    private String nodeId;
    private Boolean lider;
    private String liderAtual;
    private String ultimaExecucaoEm;
    private ReplicacaoStatus ultimoStatus;
    private String mensagem;
}
