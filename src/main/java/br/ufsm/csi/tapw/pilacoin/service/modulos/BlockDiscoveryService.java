package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.IModulo;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class BlockDiscoveryService extends IModulo {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;

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

        if (!this.modulo.isAtivo() && blocoJson != null) {
            this.queueService.publishBlocoDescoberto(blocoJson);

            return;
        }

        if (blocoJson == null ||
            blocoJson.getNomeUsuarioMinerador() != null &&
                blocoJson.getNomeUsuarioMinerador().equals(sharedUtil.getProperties().getUsername())
        ) {
            return;
        }

        blocoJson.setNonceBlocoAnterior(blocoJson.getNonce());

        Thread t = new Thread(new BlockMinerRunnable(blocoJson, this.difficulty));
        t.setName("BlockMinerThread");
        t.start();
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

            Logger.logBox("Minerando bloco... | " + JacksonUtil.toString(blocoJson));

            blocoJson.setChaveUsuarioMinerador(sharedUtil.getPublicKey().getEncoded());
            blocoJson.setNomeUsuarioMinerador(sharedUtil.getProperties().getUsername());
        }

        @Override
        @SneakyThrows
        public void run() {
            int count = 0;

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
