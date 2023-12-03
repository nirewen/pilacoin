package br.ufsm.csi.tapw.pilacoin.types;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public abstract class IModulo implements Observer<Difficulty> {
    public Modulo modulo;
    @Setter
    @JsonIgnore
    public SseEmitter logEmitter;
    @Setter
    @JsonIgnore
    private ModuloService moduloService;

    public String getNome() {
        return this.getClass().getSimpleName().replaceAll("Service", "");
    }

    public void register(Modulo modulo) {
        this.modulo = modulo;
    }

    @SneakyThrows
    public void log(ModuloLogMessage message) {
        message.setTopic(this.getNome());
        this.moduloService.log(message);
    }
}
