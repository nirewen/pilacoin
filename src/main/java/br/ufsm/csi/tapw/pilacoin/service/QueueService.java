package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.BlocoValidado;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoinValidado;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.model.json.MessageJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.model.json.ReportJson;
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
    private final RabbitTemplate rabbitTemplate;
    private final PilaCoinService pilaCoinService;
    private final SharedUtil sharedUtil;

    @Value("${queue.pila.minerado}")
    private String PILA_MINERADO;
    @Value("${queue.pila.validado}")
    private String PILA_VALIDADO;
    @Value("${queue.bloco.minerado}")
    private String BLOCO_MINERADO;
    @Value("${queue.bloco.validado}")
    private String BLOCO_VALIDADO;

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

        if (message.getQueue().equals(PILA_MINERADO)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.VALIDO);
        }

        if (message.getQueue().equals(PILA_VALIDADO)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.AG_BLOCO);
        }

        Logger.log(response);
    }

    private void handleError(MessageJson message) {
        if (message.getQueue().equals(PILA_MINERADO)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.INVALIDO);
        }

        Logger.logBox("ERRO\n---\n" + message.getErro());
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

    public void publishPilaCoinMinerado(PilaCoinJson pilaCoinJson) {
        this.publishToQueue(PILA_MINERADO, JacksonUtil.toString(pilaCoinJson));
    }

    public void publishPilaCoinValidado(PilaCoinValidado pilaCoinValidado) {
        this.publishToQueue(PILA_VALIDADO, JacksonUtil.toString(pilaCoinValidado));
    }

    public void publishBlocoMinerado(BlocoJson blocoJson) {
        this.publishToQueue(BLOCO_MINERADO, JacksonUtil.toString(blocoJson));
    }

    public void publishBlocoValidado(BlocoValidado blocoValidado) {
        this.publishToQueue(BLOCO_VALIDADO, JacksonUtil.toString(blocoValidado));
    }

    private void publishToQueue(String queue, String message) {
        this.rabbitTemplate.convertAndSend(queue, message);
    }
}
