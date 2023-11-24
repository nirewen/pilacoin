package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class MiningService implements Observer<Difficulty> {
    private final QueueService queueService;
    private final PilaCoinService pilaCoinService;
    private final SharedUtil sharedUtil;
    private final List<Thread> threads = new ArrayList<>();

    public MiningService(QueueService queueService, PilaCoinService pilaCoinService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
    }

    @Override
    public void update(Difficulty subject) {
        for (Thread t : this.threads) {
            t.interrupt();
        }

        IntStream.range(0, this.sharedUtil.getProperties().getMiningThreads()).forEach((i) -> {
            Thread t = new Thread(new MinerRunnable(subject));

            t.setName("MiningThread " + i);
            t.start();

            this.threads.add(t);
        });
    }

    public class MinerRunnable implements Runnable {
        private final Difficulty difficulty;

        public MinerRunnable(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        @Override
        @SneakyThrows
        public void run() {
            int count = 0;
            Logger.log("Minerando...");

            while (true) {
                count++;

                try {
                    PilaCoinJson pilaCoin = pilaCoinService.generatePilaCoin(this.difficulty);

                    if (pilaCoin != null) {
                        Logger.log(String.format("Encontrado Pila depois de %d tentativas", count));

                        queueService.publishPilaCoin(pilaCoin);

                        count = 0;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}
