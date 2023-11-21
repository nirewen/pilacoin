package br.ufsm.csi.tapw.pilacoin.model.json;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlocoJson {
    private String numeroBloco;
    private String nonceBlocoAnterior;
    private String nonce;
    private byte[] chaveUsuarioMinerador;
    private String nomeUsuarioMinerador;
    private List<TransacaoJson> transacoes;
}
