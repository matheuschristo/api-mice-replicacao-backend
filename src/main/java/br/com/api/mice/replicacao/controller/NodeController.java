package br.com.api.mice.replicacao.controller;

import br.com.api.mice.replicacao.dto.HeartbeatMessageDTO;
import br.com.api.mice.replicacao.dto.NodeInfoDTO;
import br.com.api.mice.replicacao.service.eleicao.HeartbeatService;
import br.com.api.mice.replicacao.service.eleicao.NodeRegistryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor
public class NodeController {

    private final HeartbeatService heartbeatService;
    private final NodeRegistryService nodeRegistryService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(@RequestBody HeartbeatMessageDTO messageDTO) {
        heartbeatService.processarHeartbeat(messageDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/leader")
    public ResponseEntity<NodeInfoDTO> leader() {
        return ResponseEntity.ok(nodeRegistryService.toDto(nodeRegistryService.obterLiderAtual()));
    }

    @GetMapping
    public ResponseEntity<List<NodeInfoDTO>> list() {
        List<NodeInfoDTO> nodes = nodeRegistryService.listarTodos().stream()
            .map(nodeRegistryService::toDto)
            .toList();
        return ResponseEntity.ok(nodes);
    }
}
