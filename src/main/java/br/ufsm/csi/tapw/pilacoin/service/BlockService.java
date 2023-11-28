package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class BlockService implements Observer<Difficulty> {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private Difficulty difficulty;

    public BlockService(QueueService queueService, SharedUtil sharedUtil) {
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

        Thread t = new Thread(new BlockMinerRunnable(blocoJson, this.difficulty));

        t.setName("BlockMinerThread");

        t.start();
    }

    @RabbitListener(queues = "${queue.bloco.minerado}")
    public void validaBloco(@Payload String json) {
//        Logger.log(json);
        if (this.difficulty == null || json == null || json.isEmpty()) {
            return;
        }
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }

    public class BlockMinerRunnable implements Runnable {
        private final Difficulty difficulty;
        private final BlocoJson blocoJson;

        public BlockMinerRunnable(BlocoJson blocoJson, Difficulty difficulty) {
            this.difficulty = difficulty;
            this.blocoJson = blocoJson;

            blocoJson.setChaveUsuarioMinerador(sharedUtil.getPublicKey().getEncoded());
            blocoJson.setNomeUsuarioMinerador(sharedUtil.getProperties().getUsername());
        }

        @Override
        @SneakyThrows
        public void run() {
            int count = 0;

            Logger.logBox("Minerando bloco...");

            while (true) {
                count++;

                blocoJson.setNonce(CryptoUtil.getRandomNonce());

                String minedJson = JacksonUtil.toString(blocoJson);

                if (CryptoUtil.compareHash(minedJson, this.difficulty.getDificuldade())) {
                    queueService.publishBlocoMinerado(blocoJson);

                    break;
                }
            }

            Logger.logBox(STR. """
                BLOCO MINERADO
                ---
                Em \{ count } tentativas
                """ );
            Thread.currentThread().interrupt();
        }
    }
}
