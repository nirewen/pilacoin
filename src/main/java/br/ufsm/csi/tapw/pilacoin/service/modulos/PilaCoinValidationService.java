package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoinValidado;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PilaCoinValidationService implements Observer<Difficulty> {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private Difficulty difficulty;

    public PilaCoinValidationService(QueueService queueService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${queue.pila.minerado}")
    public void validatePila(@Payload String json) {
        if (this.difficulty == null || json == null || json.isEmpty()) {
            return;
        }

        PilaCoinJson pilaCoinJson = JacksonUtil.convert(json, PilaCoinJson.class);

        if (pilaCoinJson == null) {
            return;
        }

        boolean valid = CryptoUtil.compareHash(json, this.difficulty.getDificuldade());

        if (pilaCoinJson.getNomeCriador().equals(this.sharedUtil.getProperties().getUsername()) || !valid) {
            this.queueService.publishPilaCoinMinerado(pilaCoinJson);

            return;
        }

        PilaCoinValidado pilaCoinValidado = PilaCoinValidado.builder()
            .nomeValidador(this.sharedUtil.getProperties().getUsername())
            .chavePublicaValidador(this.sharedUtil.getPublicKey().getEncoded())
            .assinaturaPilaCoin(CryptoUtil.sign(json, this.sharedUtil.getPrivateKey()))
            .pilaCoinJson(pilaCoinJson)
            .build();

        this.queueService.publishPilaCoinValidado(pilaCoinValidado);

        Logger.logBox(STR. """
            PILA VALIDADO
            ---
            \{ pilaCoinJson.getNomeCriador() }
            """ );
    }

    @Override
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }
}
