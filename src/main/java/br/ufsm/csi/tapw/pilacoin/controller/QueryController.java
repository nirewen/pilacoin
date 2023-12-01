package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.model.json.QueryJson;
import br.ufsm.csi.tapw.pilacoin.model.json.QueryResponseJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/query")
public class QueryController {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;

    public QueryController(QueueService queueService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.sharedUtil = sharedUtil;
    }

    @GetMapping(value = "/usuarios", produces = "application/json")
    public QueryResponseJson getUsuarios() {
        QueryJson query = QueryJson
            .builder()
            .idQuery(1L)
            .nomeUsuario(this.sharedUtil.getProperties().getUsername())
            .tipoQuery(QueryJson.TipoQuery.USUARIOS)
            .build();

        return this.queueService.requestQuery(query);
    }

    @GetMapping(value = "/pilas", produces = "application/json")
    public QueryResponseJson getPilas(@RequestParam Map<String, String> filter) {
        QueryJson query = QueryJson
            .builder()
            .idQuery(2L)
            .nomeUsuario(this.sharedUtil.getProperties().getUsername())
            .tipoQuery(QueryJson.TipoQuery.PILA)
            .build();

        if (filter.get("self") != null && filter.get("self").equals("true")) {
            query.setUsuarioMinerador(this.sharedUtil.getProperties().getUsername());
        } else {
            query.setUsuarioMinerador(filter.get("usuarioMinerador"));
        }

        return this.queueService.requestQuery(query);
    }

    @GetMapping(value = "/blocos", produces = "application/json")
    public QueryResponseJson getBlocos(@RequestParam Map<String, String> filter) {
        QueryJson query = QueryJson
            .builder()
            .idQuery(3L)
            .nomeUsuario(this.sharedUtil.getProperties().getUsername())
            .tipoQuery(QueryJson.TipoQuery.BLOCO)
            .build();
        
        if (filter.get("self") != null && filter.get("self").equals("true")) {
            query.setUsuarioMinerador(this.sharedUtil.getProperties().getUsername());
        } else {
            query.setUsuarioMinerador(filter.get("usuarioMinerador"));
        }

        return this.queueService.requestQuery(query);
    }
}
