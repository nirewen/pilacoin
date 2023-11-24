package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.MessageJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaValidado;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;
    private final PilaCoinService pilaCoinService;

    @Value("${queue.pila.minerado}")
    private String filaMinerado;
    @Value("${queue.pila.validado}")
    private String filaValidado;

    public QueueService(RabbitTemplate rabbitTemplate, PilaCoinService pilaCoinService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilaCoinService = pilaCoinService;
    }

    public void publishPilaCoin(PilaCoinJson pilaCoinJson) {
        this.rabbitTemplate.convertAndSend(filaMinerado, JacksonUtil.toString(pilaCoinJson));
    }

    public void publishPilaValidado(PilaValidado pilaValidado) {
        this.rabbitTemplate.convertAndSend(filaValidado, JacksonUtil.toString(pilaValidado));
    }

    @RabbitListener(queues = "${pilacoin.username}")
    public void receiveResponse(@Payload String response) {
        MessageJson message = JacksonUtil.convert(response, MessageJson.class);

        Logger.log(response);

        if (Objects.isNull(message)) {
            return;
        }

        if (message.getErro() != null) {
            Logger.log("[ERRO] " + response);

            return;
        }

        if (message.getQueue().equals(filaMinerado)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            if (pilaCoin != null) {
                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.VALIDO);
            }

            return;
        }

        if (message.getQueue().equals(filaValidado)) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            if (pilaCoin != null) {
                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.AG_BLOCO);
            }

            return;
        }

        Logger.log(response);
    }
}
