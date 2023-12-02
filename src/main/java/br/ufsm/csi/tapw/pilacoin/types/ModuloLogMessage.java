package br.ufsm.csi.tapw.pilacoin.types;

import lombok.Builder;

@Builder
public class ModuloLogMessage {
    @Builder.Default
    public Long timestamp = System.currentTimeMillis();
    public String title;
    public String message;
    public Object extra;
}
