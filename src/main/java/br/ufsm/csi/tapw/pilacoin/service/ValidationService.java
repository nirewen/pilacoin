package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ValidationService implements Observer<Difficulty> {
    private Difficulty difficulty;

    @RabbitListener(queues = "${queue.minerado}")
    public void validatePila(@Payload String pilaCoinJson) {
        System.out.println(pilaCoinJson);

        if (this.difficulty == null) {
            return;
        }

        boolean valid = CryptoUtil.compareHash(pilaCoinJson, this.difficulty.getDificuldade());

        if (valid) {
            System.out.println("PILA VALIDADO");

            PilaCoinJson json = JacksonUtil.convert(pilaCoinJson, PilaCoinJson.class);

            json.setChaveCriador(null);

            System.out.println(json);
        }
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }
}
