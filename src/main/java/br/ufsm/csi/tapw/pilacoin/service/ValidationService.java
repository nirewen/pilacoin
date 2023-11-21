package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ValidationService implements Observer<Difficulty> {
    private Difficulty difficulty;
    private QueueService queueService;
    private SharedUtil sharedUtil;

    public ValidationService(QueueService queueService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${queue.minerado}")
    public void validatePila(@Payload String pilaCoinJson) {
        if (this.difficulty == null) {
            return;
        }

        PilaCoinJson json = JacksonUtil.convert(pilaCoinJson, PilaCoinJson.class);

        if (json.getNomeCriador().equals(this.sharedUtil.getProperties().getUsername())) {
            this.queueService.publishPilaCoin(json);

            return;
        }

        boolean valid = CryptoUtil.compareHash(pilaCoinJson, this.difficulty.getDificuldade());

        if (valid) {
            System.out.println("PILA VALIDADO");
            System.out.println(json);
        }
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }
}
