package br.ufsm.csi.tapw.pilacoin.types;

import lombok.Builder;

import java.util.Date;

@Builder
public class ModuloLogMessage {
    public Date timestamp;
    public String message;
    public IModulo modulo;
}
