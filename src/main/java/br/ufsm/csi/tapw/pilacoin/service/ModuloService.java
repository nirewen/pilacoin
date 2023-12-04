package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.exception.ModuloNotFoundException;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.repository.ModuloRepository;
import br.ufsm.csi.tapw.pilacoin.types.AbstractSetting;
import br.ufsm.csi.tapw.pilacoin.types.AppModule;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ModuloService {
    public final Set<SseEmitter> sseEmitters = new HashSet<>();

    private final ModuloRepository moduloRepository;
    private final DifficultyService difficultyService;
    private final Map<String, AppModule> modulos = new HashMap<>();

    public ModuloService(
        ModuloRepository moduloRepository,
        DifficultyService difficultyService,
        AppModule... modulos
    ) {
        this.moduloRepository = moduloRepository;
        this.difficultyService = difficultyService;

        for (AppModule modulo : modulos) {
            modulo.register(this.registerModulo(modulo));
        }
    }

    public List<Modulo> getAllModulos() {
        return this.modulos.values().stream().map(AppModule::getModulo).toList();
    }

    public Modulo getModuloEntity(String topic) {
        return moduloRepository.findOneByTopic(topic);
    }

    public AppModule getModulo(String nome) {
        return this.modulos.get(nome);
    }

    public Modulo updateSettings(String nome, List<AbstractSetting<?>> settings) {
        Modulo modulo = this.getModuloEntity(nome);

        if (modulo == null) {
            throw new ModuloNotFoundException();
        }

        String oldSettingsStr = JacksonUtil.toString(modulo.getSettings());
        String newSettingsStr = JacksonUtil.toString(settings);

        if (oldSettingsStr.equals(newSettingsStr)) {
            return modulo;
        }

        modulo.setSettings(settings);

        moduloRepository.save(modulo);

        AppModule appModulo = this.getModulo(nome);

        appModulo.setModulo(modulo);

        appModulo.updateSettings(
            new SettingsManager(
                modulo.getSettings()
            )
        );
        appModulo.updateDifficulty(
            this.difficultyService.getDifficulty()
        );

        return modulo;
    }

    public Modulo registerModulo(AppModule modulo) {
        Modulo foundModulo = moduloRepository.findOneByTopic(modulo.getTopic());

        modulo.setModuloService(this);
        this.modulos.put(modulo.getTopic(), modulo);

        if (foundModulo != null) {
            modulo.updateSettings(
                new SettingsManager(
                    foundModulo.getSettings()
                )
            );

            return foundModulo;
        }

        Modulo moduloEntity = Modulo.builder()
            .topic(modulo.getTopic())
            .name(modulo.getName())
            .settings(
                modulo.getSettingsManager().getSettings()
            )
            .build();

        return moduloRepository.save(moduloEntity);
    }

    @SneakyThrows
    public void log(ModuloLogMessage message) {
        if (this.sseEmitters.isEmpty()) {
            return;
        }

        this.sseEmitters.forEach(emitter -> {
            if (emitter == null) {
                return;
            }

            try (ExecutorService sseExecutor = Executors.newCachedThreadPool()) {
                sseExecutor.execute(
                    () -> {
                        try {
                            emitter.send(
                                SseEmitter.event()
                                    .id("0")
                                    .name(message.getTopic())
                                    .data(message)
                                    .reconnectTime(10000)
                            );
                        } catch (Exception e) {
                            emitter.complete();
                        }
                    });
            }
        });
    }
}
