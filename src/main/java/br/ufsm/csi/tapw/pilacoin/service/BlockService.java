package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
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

        blocoJson.setChaveUsuarioMinerador(this.sharedUtil.getPublicKey().getEncoded());
        blocoJson.setNomeUsuarioMinerador(this.sharedUtil.getProperties().getUsername());

        int count = 0;

        while (true) {
            count++;

            blocoJson.setNonce(CryptoUtil.getRandomNonce());

            String minedJson = JacksonUtil.toString(blocoJson);

            if (CryptoUtil.compareHash(minedJson, this.difficulty.getDificuldade())) {
                this.queueService.publishBlocoMinerado(blocoJson);

                break;
            }
        }

        Logger.log(String.format("Bloco minerado em %d tentativas ", count));
    }

    @RabbitListener(queues = "${queue.bloco.minerado}")
    public void validaBloco(@Payload String json) {
        Logger.log(json);
        if (this.difficulty == null || json == null || json.isEmpty()) {
            return;
        }
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }
}
