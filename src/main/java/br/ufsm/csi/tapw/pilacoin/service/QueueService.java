package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.BlocoValidado;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoinValidado;
import br.ufsm.csi.tapw.pilacoin.model.Transferencia;
import br.ufsm.csi.tapw.pilacoin.model.json.*;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
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
    @Value("${queue.pila.transferir}")
    private String TRANSFERIR_PILA;

    private ReportJson lastReport;

    public QueueService(RabbitTemplate rabbitTemplate, PilaCoinService pilaCoinService, SharedUtil sharedUtil) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
    }

    @RabbitListener(queues = "${pilacoin.username}")
    public void onResponse(@Payload String response) {
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

    @RabbitListener(queues = "report")
    public void onReport(@Payload String report) {
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

    @SneakyThrows
    public QueryResponseJson requestQuery(QueryJson queryJson) {
        String json = JacksonUtil.toString(queryJson);
        this.publishToQueue("query", json);

        int tries = 0;

        while (tries++ != 10) {
            String responseJson = (String) this.rabbitTemplate.receiveAndConvert(queryJson.getNomeUsuario() + "-query");
            QueryResponseJson response = JacksonUtil.convert(responseJson, QueryResponseJson.class);

            if (response == null) {
                Thread.sleep(500);

                continue;
            }

            if (response.getIdQuery().equals(queryJson.getIdQuery())) {
                return response;
            }
        }

        return QueryResponseJson.builder()
            .idQuery(0L)
            .build();
    }

    private void handleError(MessageJson message) {
        if (message.getQueue().equals(PILA_MINERADO)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.INVALIDO);
        }

        Logger.logBox("ERRO\n---\n" + message.getErro());
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

    public void publishTransferencia(Transferencia transferencia) {
        this.publishToQueue(TRANSFERIR_PILA, JacksonUtil.toString(transferencia));
    }

    private void publishToQueue(String queue, String message) {
        this.rabbitTemplate.convertAndSend(queue, message);
    }
}
