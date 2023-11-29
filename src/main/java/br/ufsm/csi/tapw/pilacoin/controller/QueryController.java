package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.model.json.QueryJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
public class QueryController {
    private QueueService queueService;

    public QueryController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping(value = "/usuarios", produces = "application/json")
    public Object getUsuarios() {
        return this.queueService.requestQuery(
            QueryJson
                .builder()
                .idQuery(1L)
                .nomeUsuario("londeroedu")
                .tipoQuery(QueryJson.TipoQuery.USUARIOS)
                .build()
        );
    }

    @GetMapping(value = "/pilas", produces = "application/json")
    public Object getPilas() {
        return this.queueService.requestQuery(
            QueryJson
                .builder()
                .idQuery(2L)
                .nomeUsuario("londeroedu")
                .tipoQuery(QueryJson.TipoQuery.PILA)
                .build()
        );
    }

    @GetMapping(value = "/blocos", produces = "application/json")
    public Object getBlocos() {
        return this.queueService.requestQuery(
            QueryJson
                .builder()
                .idQuery(3L)
                .nomeUsuario("londeroedu")
                .tipoQuery(QueryJson.TipoQuery.BLOCO)
                .build()
        );
    }
}
