package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.impl.BooleanSetting;
import br.ufsm.csi.tapw.pilacoin.impl.RangeSetting;
import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.service.PilaCoinService;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.AppModule;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class PilaCoinMiningService extends AppModule {
    private final QueueService queueService;
    private final PilaCoinService pilaCoinService;
    private final List<PilaCoinMinerRunnable> threads = new ArrayList<>();

    public PilaCoinMiningService(QueueService queueService, PilaCoinService pilaCoinService) {
        super("Minerador de PilaCoin", new SettingsManager(
            new BooleanSetting("active", false),
            new RangeSetting("miningThreads", 8, 1, 8)
        ));

        this.queueService = queueService;
        this.pilaCoinService = pilaCoinService;
    }

    @Override
    public void updateDifficulty(Difficulty subject) {
        this.stopThreads();

        if (!this.getSettingsManager().getBoolean("active") || subject == null) {
            return;
        }

        IntStream.range(0, this.getSettingsManager().getRangeValue("miningThreads")).forEach((i) -> {
            try {
                PilaCoinMinerRunnable runnable = new PilaCoinMinerRunnable(subject);
                Thread t = new Thread(runnable);

                t.setName("MiningThread " + i);

                Thread.sleep(100);

                t.start();

                this.threads.add(runnable);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void updateSettings(SettingsManager subject) {
        this.setSettingsManager(subject);

        this.stopThreads();

        this.log(
            ModuloLogMessage.builder()
                .title("Configurações alteradas")
                .message("Clique para ver as configurações atuais")
                .extra(subject.getSettings())
                .build()
        );

        Logger.log("Configurações alteradas | " + JacksonUtil.toString(subject.getSettings()));

        if (subject.getBoolean("active")) {
            Logger.log(this.getName() + " inicializada");

            this.log(
                ModuloLogMessage.builder()
                    .title(this.getName())
                    .message("Inicializada")
                    .build()
            );
        } else {
            Logger.log(this.getName() + " desativada");

            this.log(
                ModuloLogMessage.builder()
                    .title(this.getName())
                    .message("Desativada")
                    .build()
            );
        }
    }

    private void stopThreads() {
        this.threads.forEach(PilaCoinMinerRunnable::stop);
        this.threads.clear();
    }

    public class PilaCoinMinerRunnable implements Runnable {
        private final Difficulty difficulty;
        private boolean running = true;

        public PilaCoinMinerRunnable(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        @Override
        @SneakyThrows
        public void run() {
            int count = 0;

            Logger.log("Minerando...");
            log(
                ModuloLogMessage.builder()
                    .title(Thread.currentThread().getName())
                    .message("Minerando PilaCoin")
                    .extra(difficulty)
                    .build()
            );

            while (running) {
                count++;

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                try {
                    PilaCoinJson pilaCoin = pilaCoinService.generatePilaCoin(this.difficulty);

                    if (pilaCoin != null) {
                        Logger.log("PilaCoin minerado em " + count + " tentativas");
                        log(
                            ModuloLogMessage.builder()
                                .title("PilaCoin minerado")
                                .message("em " + count + " tentativas")
                                .extra(pilaCoin)
                                .build()
                        );

                        queueService.publishPilaCoinMinerado(pilaCoin);

                        count = 0;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        public void stop() {
            this.running = false;
        }
    }
}
