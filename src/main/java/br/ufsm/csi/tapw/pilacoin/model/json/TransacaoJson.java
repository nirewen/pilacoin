package br.ufsm.csi.tapw.pilacoin.model.json;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TransacaoJson {
    private String id;
    private byte[] chaveUsuarioOrigem;
    private byte[] chaveUsuarioDestino;
    private byte[] assinatura;
    private String origem;
    private String noncePila;
    private String status;
    private Date dataTransacao;
}
