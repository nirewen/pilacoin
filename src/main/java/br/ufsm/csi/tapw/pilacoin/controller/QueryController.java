package br.ufsm.csi.tapw.pilacoin.controller;

import br.ufsm.csi.tapw.pilacoin.dto.QueryResponse;
import br.ufsm.csi.tapw.pilacoin.model.json.*;
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

    @GetMapping("/usuarios")
    public QueryResponse<UsuarioJson> getUsuarios() {
        QueryJson query = QueryJson
            .builder()
            .idQuery(System.nanoTime() - 100)
            .nomeUsuario(this.sharedUtil.getProperties().getUsername())
            .tipoQuery(QueryJson.TipoQuery.USUARIOS)
            .build();

        QueryResponseJson response = this.queueService.requestQuery(query);

        return QueryResponse
            .<UsuarioJson>builder()
            .idQuery(response.getIdQuery())
            .usuario(response.getUsuario())
            .result(response.getUsuariosResult())
            .build();
    }

    @GetMapping("/pilas")
    public QueryResponse<PilaCoinJson> getPilas(@RequestParam Map<String, String> filter) {
        QueryJson query = QueryJson
            .builder()
            .idQuery(System.nanoTime() - 200)
            .nomeUsuario(this.sharedUtil.getProperties().getUsername())
            .tipoQuery(QueryJson.TipoQuery.PILA)
            .build();

        if (filter.get("self") != null && filter.get("self").equals("true")) {
            query.setUsuarioMinerador(this.sharedUtil.getProperties().getUsername());
        } else {
            query.setUsuarioMinerador(filter.get("usuarioMinerador"));
        }

        QueryResponseJson response = this.queueService.requestQuery(query);

        return QueryResponse
            .<PilaCoinJson>builder()
            .idQuery(response.getIdQuery())
            .usuario(response.getUsuario())
            .result(response.getPilasResult())
            .build();
    }

    @GetMapping("/blocos")
    public QueryResponse<BlocoJson> getBlocos(@RequestParam Map<String, String> filter) {
        QueryJson query = QueryJson
            .builder()
            .idQuery(System.nanoTime() - 300)
            .nomeUsuario(this.sharedUtil.getProperties().getUsername())
            .tipoQuery(QueryJson.TipoQuery.BLOCO)
            .build();

        if (filter.get("self") != null && filter.get("self").equals("true")) {
            query.setUsuarioMinerador(this.sharedUtil.getProperties().getUsername());
        } else {
            query.setUsuarioMinerador(filter.get("usuarioMinerador"));
        }

        QueryResponseJson response = this.queueService.requestQuery(query);

        return QueryResponse
            .<BlocoJson>builder()
            .idQuery(response.getIdQuery())
            .usuario(response.getUsuario())
            .result(response.getBlocosResult())
            .build();
    }
}
