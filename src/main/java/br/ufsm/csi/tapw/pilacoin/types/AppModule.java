package br.ufsm.csi.tapw.pilacoin.types;

import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import br.ufsm.csi.tapw.pilacoin.types.observer.DifficultyObserver;
import br.ufsm.csi.tapw.pilacoin.types.observer.SettingsObserver;
import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public abstract class AppModule implements DifficultyObserver, SettingsObserver {
    @Getter @Setter
    public Modulo modulo;
    @Setter
    @JsonIgnore
    public SseEmitter logEmitter;
    @Setter
    @JsonIgnore
    private ModuloService moduloService;

    @Getter
    private String name;
    @Getter @Setter
    private SettingsManager settingsManager;
    @Getter
    private String topic = this.getClass().getSimpleName().replaceAll("Service", "");

    public AppModule(String name, SettingsManager settingsManager) {
        this.name = name;
        this.settingsManager = settingsManager;
    }

    public void register(Modulo modulo) {
        this.modulo = modulo;
    }

    @SneakyThrows
    public void log(ModuloLogMessage message) {
        message.setTopic(this.getTopic());

        this.moduloService.log(message);
    }
}
