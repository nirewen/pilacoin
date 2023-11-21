package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaValidado;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ValidationService implements Observer<Difficulty> {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private Difficulty difficulty;

    public ValidationService(QueueService queueService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${queue.minerado}")
    public void validatePila(@Payload String pilaCoinJson) {
        if (this.difficulty == null || pilaCoinJson == null || pilaCoinJson.isEmpty()) {
            return;
        }

        PilaCoinJson json = JacksonUtil.convert(pilaCoinJson, PilaCoinJson.class);
        boolean valid = CryptoUtil.compareHash(pilaCoinJson, this.difficulty.getDificuldade());

        if (json.getNomeCriador().equals(this.sharedUtil.getProperties().getUsername()) || !valid) {
            this.queueService.publishPilaCoin(json);

            return;
        }

        PilaValidado pilaValidado = PilaValidado.builder()
            .nomeValidador(this.sharedUtil.getProperties().getUsername())
            .chavePublicaValidador(this.sharedUtil.getPublicKey().getEncoded())
            .assinaturaPilaCoin(CryptoUtil.sign(pilaCoinJson, this.sharedUtil.getPrivateKey()))
            .pilaCoinJson(pilaCoinJson)
            .build();

        this.queueService.validarPilaCoin(pilaValidado);

        System.out.println("PILA VALIDADO!");
        System.out.println(json.getNomeCriador());
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }
}
