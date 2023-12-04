package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.impl.BooleanSetting;
import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.AppModule;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.util.*;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockDiscoveryService extends AppModule {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private final List<BlockMinerRunnable> threads = new ArrayList<>();

    private Difficulty difficulty;

    public BlockDiscoveryService(QueueService queueService, SharedUtil sharedUtil) {
        super("Descobridor de Bloco", new SettingsManager(
            new BooleanSetting("active", false)
        ));
        this.queueService = queueService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${queue.bloco.descobre}")
    public void descobreBloco(@Payload String json) {
        if (this.difficulty == null || json == null || json.isEmpty()) {
            return;
        }

        BlocoJson blocoJson = JacksonUtil.convert(json, BlocoJson.class);

        if (blocoJson == null) {
            return;
        }

        if (!this.getSettingsManager().getBoolean("active")) {
            this.queueService.publishBlocoDescoberto(blocoJson);

            return;
        }

        if (blocoJson.getNomeUsuarioMinerador() != null &&
            blocoJson.getNomeUsuarioMinerador().equals(sharedUtil.getProperties().getUsername())
        ) {
            return;
        }

        blocoJson.setNonceBlocoAnterior(blocoJson.getNonce());

        BlockMinerRunnable runnable = new BlockMinerRunnable(blocoJson, this.difficulty);
        Thread t = new Thread(runnable);
        t.setName("BlockMinerThread");
        t.start();

        this.threads.add(runnable);
    }

    @Override
    public void updateDifficulty(Difficulty subject) {
        this.difficulty = subject;

        this.stopThreads();
    }

    @Override
    public void onUpdateSettings(SettingsManager subject) {
        this.stopThreads();
    }

    private void stopThreads() {
        this.threads.forEach(BlockMinerRunnable::stop);
        this.threads.clear();
    }

    public class BlockMinerRunnable implements Runnable {
        private final Difficulty difficulty;
        private final BlocoJson blocoJson;

        private boolean running = true;
        private boolean stopped = false;

        public BlockMinerRunnable(BlocoJson blocoJson, Difficulty difficulty) {
            this.difficulty = difficulty;
            this.blocoJson = blocoJson;
        }

        @Override
        @SneakyThrows
        public void run() {
            int count = 0;

            Logger.log("Minerando bloco... | " + JacksonUtil.toString(blocoJson));
            log(
                ModuloLogMessage.builder()
                    .title(Thread.currentThread().getName() + " " + Thread.currentThread().threadId())
                    .message("Minerando bloco nº " + blocoJson.getNumeroBloco())
                    .extra(blocoJson)
                    .build()
            );

            blocoJson.setChaveUsuarioMinerador(sharedUtil.getPublicKey().getEncoded());
            blocoJson.setNomeUsuarioMinerador(sharedUtil.getProperties().getUsername());

            while (running) {
                count++;

                blocoJson.setNonce(CryptoUtil.getRandomNonce());

                String minedJson = JacksonUtil.toString(blocoJson);

                if (CryptoUtil.compareHash(minedJson, this.difficulty.getDificuldade())) {
                    queueService.publishBlocoMinerado(blocoJson);

                    break;
                }
            }

            if (this.stopped) {
                return;
            }

            Logger.log("Bloco nº " + blocoJson.getNumeroBloco() + " minerado em " + count + " tentativas");
            log(
                ModuloLogMessage.builder()
                    .title("Bloco nº " + blocoJson.getNumeroBloco() + " minerado")
                    .message("em " + count + " tentativas")
                    .extra(blocoJson)
                    .build()
            );
            this.running = false;
        }

        public void stop() {
            this.running = false;
            this.stopped = true;
        }
    }
}
