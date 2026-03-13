package br.com.api.mice.replicacao.service;

import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import br.com.api.mice.replicacao.entity.enums.TipoEntidadeReplicacao;
import br.com.api.mice.replicacao.service.replicacao.CidadeReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.EmpresaReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.EstadoReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.FilialReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.PaisReplicacaoService;
import br.com.api.mice.replicacao.service.replicacao.UsuarioReplicacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplicacaoDispatcherService {

    private final PaisReplicacaoService paisReplicacaoService;
    private final EstadoReplicacaoService estadoReplicacaoService;
    private final CidadeReplicacaoService cidadeReplicacaoService;
    private final EmpresaReplicacaoService empresaReplicacaoService;
    private final FilialReplicacaoService filialReplicacaoService;
    private final UsuarioReplicacaoService usuarioReplicacaoService;

    public void dispatch(EventoReplicacaoDTO evento) {
        TipoEntidadeReplicacao entidade = evento.getEntity();
        switch (entidade) {
            case PAIS -> paisReplicacaoService.processarEvento(evento);
            case ESTADO -> estadoReplicacaoService.processarEvento(evento);
            case CIDADE -> cidadeReplicacaoService.processarEvento(evento);
            case EMPRESA -> empresaReplicacaoService.processarEvento(evento);
            case FILIAL -> filialReplicacaoService.processarEvento(evento);
            case USUARIO -> usuarioReplicacaoService.processarEvento(evento);
        }
    }
}
