package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.*;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class QueueService {
    @Value("${queue.pila.minerado}")
    public final String PILA_MINERADO = "PILA_MINERADO";
    @Value("${queue.pila.validado}")
    public final String PILA_VALIDADO = "PILA_VALIDADO";
    @Value("${queue.bloco.minerado}")
    public final String BLOCO_MINERADO = "BLOCO_MINERADO";
    @Value("${queue.bloco.validado}")
    public final String BLOCO_VALIDADO = "BLOCO_VALIDADO";

    private final RabbitTemplate rabbitTemplate;
    private final PilaCoinService pilaCoinService;
    private final SharedUtil sharedUtil;
    private ReportJson lastReport;

    public QueueService(RabbitTemplate rabbitTemplate, PilaCoinService pilaCoinService, SharedUtil sharedUtil) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${pilacoin.username}")
    public void receiveResponse(@Payload String response) {
        MessageJson message = JacksonUtil.convert(response, MessageJson.class);

        if (Objects.isNull(message)) {
            return;
        }

        if (message.getErro() != null) {
            this.handleError(message);

            return;
        }

        switch (message.getQueue()) {
            case PILA_MINERADO: {
                PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.VALIDO);

                break;
            }
            case PILA_VALIDADO: {
                PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.AG_BLOCO);

                break;
            }
        }

        Logger.log(response);
    }

    private void handleError(MessageJson message) {
        switch (message.getQueue()) {
            case PILA_MINERADO: {
                PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.INVALIDO);

                break;
            }
            case PILA_VALIDADO: {
                Logger.logBox("ERRO\n---\n" + message.getErro());

                break;
            }
        }
    }

    @RabbitListener(queues = "report")
    public void receiveReport(@Payload String report) {
        List<ReportJson> reports = JacksonUtil.convert(report, new TypeReference<List<ReportJson>>() {
        });

        if (Objects.isNull(reports)) {
            return;
        }

        ReportJson newReport = reports.stream()
            .filter((r) -> r.getNomeUsuario() != null)
            .filter((r) -> r.getNomeUsuario().equals(this.sharedUtil.getProperties().getUsername()))
            .findFirst()
            .orElse(null);

        if (newReport != null) {
            if (this.lastReport == null || !newReport.compareTo(this.lastReport)) {
                this.lastReport = newReport;

                newReport.printReport();
            }
        }
    }

    public void publishPilaCoin(PilaCoinJson pilaCoinJson) {
        this.rabbitTemplate.convertAndSend(PILA_MINERADO, JacksonUtil.toString(pilaCoinJson));
    }

    public void publishPilaValidado(PilaValidado pilaValidado) {
        this.rabbitTemplate.convertAndSend(PILA_VALIDADO, JacksonUtil.toString(pilaValidado));
    }

    public void publishBlocoMinerado(BlocoJson blocoJson) {
        this.rabbitTemplate.convertAndSend(BLOCO_MINERADO, JacksonUtil.toString(blocoJson));
    }
}
