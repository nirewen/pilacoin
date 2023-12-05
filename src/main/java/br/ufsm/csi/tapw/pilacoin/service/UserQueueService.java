package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.MessageJson;
import br.ufsm.csi.tapw.pilacoin.model.json.ReportJson;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage;
import br.ufsm.csi.tapw.pilacoin.types.ModuloLogMessage.LogLevel;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import br.ufsm.csi.tapw.pilacoin.util.jackson.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserQueueService {
    private final PilaCoinService pilaCoinService;
    private final SharedUtil sharedUtil;
    private final ModuloService moduloService;

    @Value("${queue.pila.minerado}")
    private String PILA_MINERADO;
    @Value("${queue.pila.validado}")
    private String PILA_VALIDADO;

    private ReportJson lastReport;

    public UserQueueService(PilaCoinService pilaCoinService, SharedUtil sharedUtil, ModuloService moduloService) {
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
        this.moduloService = moduloService;
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

        Logger.log(message.getMsg());
        this.moduloService.log(
            ModuloLogMessage.builder()
                .topic("UserMessage")
                .title(message.getQueue())
                .message(message.getMsg())
                .extra(message.getNonce())
                .build()
        );
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

                this.moduloService.log(
                    ModuloLogMessage.builder()
                        .topic("UserMessage")
                        .title("Report")
                        .message("Relatório de atividades")
                        .extra(newReport)
                        .build()
                );
            }
        }
    }

    private void handleError(MessageJson message) {
        if (message.getQueue().equals(PILA_MINERADO)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.INVALIDO);
        }

        Logger.log(message.getErro());
        this.moduloService.log(
            ModuloLogMessage.builder()
                .topic("UserMessage")
                .title(message.getQueue())
                .message(message.getErro())
                .extra(message.getNonce())
                .level(LogLevel.ERROR)
                .build()
        );
    }
}
