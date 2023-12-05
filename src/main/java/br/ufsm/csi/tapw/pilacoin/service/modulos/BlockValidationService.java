package br.ufsm.csi.tapw.pilacoin.service.modulos;

import br.ufsm.csi.tapw.pilacoin.impl.BooleanSetting;
import br.ufsm.csi.tapw.pilacoin.model.BlocoValidado;
import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.service.QueueService;
import br.ufsm.csi.tapw.pilacoin.types.AppModule;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
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
public class BlockValidationService extends AppModule {
    private final QueueService queueService;
    private final SharedUtil sharedUtil;
    private final Stack<String> blockBlacklist = new Stack<>();

    private Difficulty difficulty;

    public BlockValidationService(QueueService queueService, SharedUtil sharedUtil) {
        super("Validador de Bloco", new SettingsManager(
            new BooleanSetting("active", false)
        ));

        this.queueService = queueService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${queue.bloco.minerado}")
    public void validarBloco(@Payload String json) {
        if (this.difficulty == null || json == null || json.isEmpty()) {
            return;
        }

        BlocoJson blocoJson = JacksonUtil.convert(json, BlocoJson.class);

        if (blocoJson == null) {
            return;
        }

        if (!this.getSettingsManager().getBoolean("active") || this.blockBlacklist.contains(blocoJson.getNonce())) {
            this.queueService.publishBlocoMinerado(blocoJson);

            return;
        }

        this.blockBlacklist.push(blocoJson.getNonce());

        boolean valid = CryptoUtil.compareHash(json, this.difficulty.getDificuldade());

        if (blocoJson.getNomeUsuarioMinerador().equals(this.sharedUtil.getProperties().getUsername()) || !valid) {
            this.queueService.publishBlocoMinerado(blocoJson);

            return;
        }

        BlocoValidado blocoValidado = BlocoValidado.builder()
            .nomeValidador(this.sharedUtil.getProperties().getUsername())
            .chavePublicaValidador(this.sharedUtil.getPublicKey().getEncoded())
            .assinaturaBloco(CryptoUtil.sign(json, this.sharedUtil.getPrivateKey()))
            .bloco(blocoJson)
            .build();

        this.queueService.publishBlocoValidado(blocoValidado);

        Logger.log("Bloco de " + blocoJson.getNomeUsuarioMinerador() + " validado.");
        this.log(
            ModuloLogMessage.builder()
                .title("Bloco validado")
                .message("Bloco de " + blocoJson.getNomeUsuarioMinerador() + " validado.")
                .extra(Map.of(
                    "bloco", blocoJson,
                    "blocoValidado", blocoValidado
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

    }
}
