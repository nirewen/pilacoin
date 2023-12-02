package br.ufsm.csi.tapw.pilacoin.types;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.SneakyThrows;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;

public abstract class IModulo implements Observer<Difficulty> {
    public Modulo modulo;
    @JsonIgnore
    public SseEmitter logEmitter = new SseEmitter(-1L);

    public String getNome() {
        return this.getClass().getSimpleName().replaceAll("Service", "");
    }

    public void register(Modulo modulo) {
        this.modulo = modulo;
    }

    @SneakyThrows
    public void log(String message) {
        this.logEmitter.send(
            ModuloLogMessage.builder()
                .timestamp(new Date(System.currentTimeMillis()))
                .message(message)
                .modulo(this)
                .build()
        );
    }
}
