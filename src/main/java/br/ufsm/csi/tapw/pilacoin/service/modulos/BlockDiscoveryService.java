package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockDiscoveryService extends IModulo {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private final List<BlockMinerRunnable> threads = new ArrayList<>();

    private Difficulty difficulty;

    public BlockDiscoveryService(QueueService queueService, SharedUtil sharedUtil) {
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

        if (!this.modulo.isAtivo()) {
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
    public void update(Difficulty subject) {
        this.difficulty = subject;

        this.threads.forEach(BlockMinerRunnable::stop);
        this.threads.clear();

        if (!this.modulo.isAtivo() || subject == null) {
            return;
        }

        this.log(
            ModuloLogMessage.builder()
                .title("Descoberta de Bloco")
                .message("Inicializada")
                .build()
        );
    }

    public class BlockMinerRunnable implements Runnable {
        private final Difficulty difficulty;
        private final BlocoJson blocoJson;

        private boolean running = true;

        public BlockMinerRunnable(BlocoJson blocoJson, Difficulty difficulty) {
            this.difficulty = difficulty;
            this.blocoJson = blocoJson;

            Logger.log("Minerando bloco... | " + JacksonUtil.toString(blocoJson));
            log(
                ModuloLogMessage.builder()
                    .title("Descoberta de Bloco")
                    .message("Minerando bloco...")
                    .extra(blocoJson)
                    .build()
            );

            blocoJson.setChaveUsuarioMinerador(sharedUtil.getPublicKey().getEncoded());
            blocoJson.setNomeUsuarioMinerador(sharedUtil.getProperties().getUsername());
        }

        @Override
        @SneakyThrows
        public void run() {
            int count = 0;

            while (running) {
                count++;

                blocoJson.setNonce(CryptoUtil.getRandomNonce());

                String minedJson = JacksonUtil.toString(blocoJson);

                if (CryptoUtil.compareHash(minedJson, this.difficulty.getDificuldade())) {
                    queueService.publishBlocoMinerado(blocoJson);

                    break;
                }
            }

            Logger.log("Bloco minerado em " + count + " tentativas");
            log(
                ModuloLogMessage.builder()
                    .title("Descoberta de Bloco")
                    .message("Bloco minerado em " + count + " tentativas")
                    .extra(blocoJson)
                    .build()
            );
            this.running = false;
        }

        public void stop() {
            this.running = false;
        }
    }
}
