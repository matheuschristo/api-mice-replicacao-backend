package br.com.api.mice.replicacao.controller;

import br.com.api.mice.replicacao.dto.CoordinatorMessageDTO;
import br.com.api.mice.replicacao.dto.ElectionMessageDTO;
import br.com.api.mice.replicacao.service.eleicao.BullyElectionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/election")
@RequiredArgsConstructor
public class ElectionController {

    private final BullyElectionService bullyElectionService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> start(@RequestBody ElectionMessageDTO messageDTO) {
        bullyElectionService.responderEleicao(messageDTO);
        return ResponseEntity.ok(Map.of("status", "election_received"));
    }

    @PostMapping("/ok")
    public ResponseEntity<Map<String, String>> ok(@RequestBody ElectionMessageDTO messageDTO) {
        bullyElectionService.registrarRespostaEleicao(messageDTO);
        return ResponseEntity.ok(Map.of("status", "ok_received"));
    }

    @PostMapping("/coordinator")
    public ResponseEntity<Map<String, String>> coordinator(@RequestBody CoordinatorMessageDTO messageDTO) {
        bullyElectionService.definirCoordenador(messageDTO);
        return ResponseEntity.ok(Map.of("status", "coordinator_updated"));
    }
}
