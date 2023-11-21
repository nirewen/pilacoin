package br.ufsm.csi.tapw.pilacoin.model.json;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PilaValidado {
    private String nomeValidador;
    private byte[] chavePublicaValidador;
    private byte[] assinaturaPilaCoin;
    private String pilaCoinJson;
}
