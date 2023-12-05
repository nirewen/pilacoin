package br.ufsm.csi.tapw.pilacoin.types;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuloLogMessage {
    @Builder.Default
    public Long timestamp = System.currentTimeMillis();
    public String topic;
    public String title;
    public String message;
    @Builder.Default
    public LogLevel level = LogLevel.INFO;
    public Object extra;

    public enum LogLevel {
        INFO,
        ERROR
    }
}
