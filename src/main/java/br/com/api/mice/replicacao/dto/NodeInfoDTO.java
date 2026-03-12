package br.com.api.mice.replicacao.dto;

import br.com.api.mice.replicacao.entity.enums.NodeStatus;
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
public class NodeInfoDTO {

    private String nodeId;
    private String host;
    private Integer porta;
    private Boolean leader;
    private NodeStatus status;
    private String lastHeartbeatAt;
}
