package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.impl.BooleanSetting;
import br.ufsm.csi.tapw.pilacoin.impl.ConstantSetting;
import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoinValidado;
import br.ufsm.csi.tapw.pilacoin.model.internal.AppModule;
import br.ufsm.csi.tapw.pilacoin.model.internal.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import br.ufsm.csi.tapw.pilacoin.util.jackson.JacksonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Stack;

@Service
public class PilaCoinValidationService extends AppModule {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private final Stack<String> pilaBlacklist = new Stack<>();

    private Difficulty difficulty;

    public PilaCoinValidationService(QueueService queueService, SharedUtil sharedUtil) {
        super("Validador de PilaCoin", new SettingsManager(
            new BooleanSetting("active", false),
            new ConstantSetting("order", 2)
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

        if (!this.getSettingsManager().getBoolean("active") || this.pilaBlacklist.contains(pilaCoinJson.getNonce())) {
            this.queueService.publishPilaCoinMinerado(pilaCoinJson);

            return;
        }

        this.pilaBlacklist.push(pilaCoinJson.getNonce());

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
    public void update(Difficulty subject) {
        this.difficulty = subject;
    }

    @Override
    public void onUpdateSettings(SettingsManager subject) {

    }
}
