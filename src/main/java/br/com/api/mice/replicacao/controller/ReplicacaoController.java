package br.com.api.mice.replicacao.controller;

import br.com.api.mice.replicacao.dto.ReplicacaoStatusDTO;
import br.com.api.mice.replicacao.service.ReplicacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replicacao")
@RequiredArgsConstructor
public class ReplicacaoController {

    private final ReplicacaoService replicacaoService;

    @PostMapping("/executar")
    public ResponseEntity<ReplicacaoStatusDTO> executar() {
        return ResponseEntity.ok(replicacaoService.executarReplicacaoCompleta());
    }

    @GetMapping("/status")
    public ResponseEntity<ReplicacaoStatusDTO> status() {
        return ResponseEntity.ok(replicacaoService.obterStatus());
    }
}
