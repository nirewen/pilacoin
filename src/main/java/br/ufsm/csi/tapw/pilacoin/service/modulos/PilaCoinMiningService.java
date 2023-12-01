package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.service.PilaCoinService;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class PilaCoinMiningService extends IModulo {
    private final QueueService queueService;
    private final PilaCoinService pilaCoinService;
    private final SharedUtil sharedUtil;
    private final List<PilaCoinMinerRunnable> threads = new ArrayList<>();

    public PilaCoinMiningService(QueueService queueService, PilaCoinService pilaCoinService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
    }

    @Override
    public void update(Difficulty subject) {
        for (PilaCoinMinerRunnable runnable : this.threads) {
            runnable.stop();
        }

        this.threads.clear();

        if (!this.modulo.isAtivo() || subject == null) {
            return;
        }

        IntStream.range(0, this.sharedUtil.getProperties().getMiningThreads()).forEach((i) -> {
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
            Logger.logBox("Minerando...");

            while (running) {
                count++;

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                try {
                    PilaCoinJson pilaCoin = pilaCoinService.generatePilaCoin(this.difficulty);

                    if (pilaCoin != null) {
                        Logger.logBox(STR. """
                            PILA MINERADO
                            ---
                            Em \{ count } tentativas
                            """ );

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
