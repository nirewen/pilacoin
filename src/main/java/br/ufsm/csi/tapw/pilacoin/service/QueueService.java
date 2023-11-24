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

    public QueueService(RabbitTemplate rabbitTemplate, PilaCoinService pilaCoinService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilaCoinService = pilaCoinService;
    }

    public void publishPilaCoin(PilaCoinJson pilaCoinJson) {
        this.rabbitTemplate.convertAndSend(Fila.PILA_MINERADO, JacksonUtil.toString(pilaCoinJson));
    }

    public void publishPilaValidado(PilaValidado pilaValidado) {
        this.rabbitTemplate.convertAndSend(Fila.PILA_VALIDADO, JacksonUtil.toString(pilaValidado));
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
            case Fila.PILA_MINERADO: {
                PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.VALIDO);

                break;
            }
            case Fila.PILA_VALIDADO: {
                PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.AG_BLOCO);

                break;
            }
        }

        Logger.log(response);
    }

    private void handleError(MessageJson message) {
        switch (message.getQueue()) {
            case Fila.PILA_MINERADO: {
                PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

                this.pilaCoinService.changeStatus(pilaCoin, PilaCoin.Status.INVALIDO);

                break;
            }
            case Fila.PILA_VALIDADO: {
                Logger.log("[ERRO] " + message.getErro());

                break;
            }
        }
    }

    private class Fila {
        @Value("${queue.pila.minerado}")
        public static final String PILA_MINERADO = "${queue.pila.minerado}";
        @Value("${queue.pila.validado}")
        public static final String PILA_VALIDADO = "${queue.pila.validado}";
    }
}
