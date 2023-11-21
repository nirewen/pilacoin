package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class BlockService implements Observer<Difficulty> {
    private final QueueService queueService;
    private Difficulty difficulty;

    public BlockService(QueueService queueService) {
        this.queueService = queueService;
    }

    @RabbitListener(queues = "${queue.bloco.descobre}")
    public void descobreBloco(@Payload String blocoJson) {
        if (this.difficulty == null || blocoJson == null || blocoJson.isEmpty()) {
            return;
        }

        BlocoJson bloco = JacksonUtil.convert(blocoJson, BlocoJson.class);

        System.out.println(bloco);
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }
}
