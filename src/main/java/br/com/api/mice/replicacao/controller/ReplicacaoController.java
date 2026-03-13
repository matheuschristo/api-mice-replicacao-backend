package br.com.api.mice.replicacao.controller;

import br.com.api.mice.replicacao.dto.ReplicacaoStatusDTO;
import br.com.api.mice.replicacao.entity.ReplicacaoLogEntity;
import br.com.api.mice.replicacao.service.ReplicacaoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replicacao")
@RequiredArgsConstructor
public class ReplicacaoController {

    private final ReplicacaoService replicacaoService;

    @GetMapping("/status")
    public ResponseEntity<ReplicacaoStatusDTO> status() {
        return ResponseEntity.ok(replicacaoService.obterStatus());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ReplicacaoLogEntity>> logs() {
        return ResponseEntity.ok(replicacaoService.buscarUltimosLogs());
    }
}
