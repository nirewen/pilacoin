package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.impl.BooleanSetting;
import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoinValidado;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.AppModule;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.util.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PilaCoinValidationService extends AppModule {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private Difficulty difficulty;

    public PilaCoinValidationService(QueueService queueService, SharedUtil sharedUtil) {
        super("Validador de PilaCoin", new SettingsManager(
            new BooleanSetting("active", false)
        ));

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

        if (!this.getSettingsManager().getBoolean("active")) {
            this.queueService.publishPilaCoinMinerado(pilaCoinJson);

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

        Logger.log("PilaCoin de " + pilaCoinJson.getNomeCriador() + " validado.");
        this.log(
            ModuloLogMessage.builder()
                .title("PilaCoin validado")
                .message("PilaCoin de " + pilaCoinJson.getNomeCriador() + " validado.")
                .extra(Map.of(
                    "pilaCoin", pilaCoinJson,
                    "pilaValidado", pilaCoinValidado
                ))
                .build()
        );
    }

    @Override
    public void updateDifficulty(Difficulty subject) {
        this.difficulty = subject;
    }

    @Override
    public void onUpdateSettings(SettingsManager subject) {
        this.setSettingsManager(subject);
    }
}
