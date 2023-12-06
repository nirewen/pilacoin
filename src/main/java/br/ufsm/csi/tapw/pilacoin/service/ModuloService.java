package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.exception.ModuloNotFoundException;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;
import br.ufsm.csi.tapw.pilacoin.model.internal.AbstractSetting;
import br.ufsm.csi.tapw.pilacoin.model.internal.AppModule;
import br.ufsm.csi.tapw.pilacoin.model.internal.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.repository.ModuloRepository;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;
import br.ufsm.csi.tapw.pilacoin.util.SizedStack;
import br.ufsm.csi.tapw.pilacoin.util.jackson.JacksonUtil;
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
    private final Stack<ModuloLogMessage> logMessages = new SizedStack<>(1000);

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
        AppModule appModulo = this.getModulo(nome);

        if (modulo == null) {
            throw new ModuloNotFoundException();
        }

        SettingsManager newManager = new SettingsManager(settings);
        SettingsManager oldManager = new SettingsManager(modulo.getSettings());
        SettingsManager difference = newManager.difference(oldManager);

        if (!difference.getSettings().isEmpty()) {
            modulo.setSettings(settings);

            moduloRepository.save(modulo);

            appModulo.setModulo(modulo);
            appModulo.setSettingsManager(newManager);

            this.log(
                ModuloLogMessage.builder()
                    .title("Configurações alteradas")
                    .message("Clique para ver as configurações atuais")
                    .extra(newManager.getSettings())
                    .build()
            );

            Logger.log("Configurações alteradas | " + JacksonUtil.toString(newManager.getSettings()));

            if (newManager.difference(oldManager).containsCritical()) {
                appModulo.onUpdateSettings(newManager);
                appModulo.update(this.difficultyService.getDifficulty());

                if (newManager.getBoolean("active")) {
                    Logger.log(appModulo.getName() + " inicializada");

                    appModulo.log(
                        ModuloLogMessage.builder()
                            .title(appModulo.getName())
                            .message("Inicializado")
                            .build()
                    );
                } else {
                    Logger.log("Módulo " + appModulo.getName() + " desativado");

                    appModulo.log(
                        ModuloLogMessage.builder()
                            .title(appModulo.getName())
                            .message("Desativado")
                            .build()
                    );
                }
            }
        }


        return modulo;
    }

    public Modulo registerModulo(AppModule modulo) {
        Modulo foundModulo = moduloRepository.findOneByTopic(modulo.getTopic());

        modulo.setModuloService(this);
        this.modulos.put(modulo.getTopic(), modulo);

        if (foundModulo != null) {
            modulo.setSettingsManager(
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

    public SseEmitter onConnect() {
        final SseEmitter sseEmitter = new SseEmitter(-1L);

        sseEmitter.onCompletion(() -> {
            synchronized (this.sseEmitters) {
                this.sseEmitters.remove(sseEmitter);
            }
        });
        sseEmitter.onTimeout(sseEmitter::complete);

        this.sseEmitters.add(sseEmitter);

        this.logMessages.forEach(message -> {
            try {
                sseEmitter.send(
                    SseEmitter.event()
                        .id("0")
                        .name(message.getTopic())
                        .data(message)
                        .reconnectTime(10000)
                );
            } catch (Exception e) {
                sseEmitter.complete();
            }
        });

        this.log(
            ModuloLogMessage.builder()
                .topic("UserMessage")
                .title("Conectado")
                .message("Conectado ao servidor de logs")
                .build()
        );

        return sseEmitter;
    }

    @SneakyThrows
    public void log(ModuloLogMessage message) {
        this.logMessages.push(message);

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
