package br.ufsm.csi.tapw.pilacoin.model.internal;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.service.ModuloService;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.types.SettingsUpdateListener;
import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public abstract class AppModule implements Observer<Difficulty>, SettingsUpdateListener {
    @Getter
    private final String name;
    @Getter
    private final String topic = this.getClass().getSimpleName().replaceAll("Service", "");
    @Getter @Setter
    public Modulo modulo;
    @Setter
    @JsonIgnore
    private ModuloService moduloService;
    @Getter @Setter
    private SettingsManager settingsManager;

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
