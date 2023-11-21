package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.MessageJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaValidado;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;
    private final PilaCoinService pilaCoinService;

    @Value("${queue.minerado}")
    private String filaMinerado;
    @Value("${queue.validado}")
    private String filaValidado;

    public QueueService(RabbitTemplate rabbitTemplate, PilaCoinService pilaCoinService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilaCoinService = pilaCoinService;
    }

    public void publishPilaCoin(PilaCoinJson pilaCoinJson) {
        this.rabbitTemplate.convertAndSend(filaMinerado, JacksonUtil.toString(pilaCoinJson));
    }

    public void validarPilaCoin(PilaValidado pilaValidado) {
        this.rabbitTemplate.convertAndSend(filaValidado, JacksonUtil.toString(pilaValidado));
    }

    @RabbitListener(queues = "${pilacoin.username}")
    public void receiveResponse(@Payload String response) {
        MessageJson message = JacksonUtil.convert(response, MessageJson.class);

        System.out.println(message);

        if (message.getMsg().startsWith("Pila validado com sucesso.")) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            if (pilaCoin != null) {
                pilaCoin.setStatus(PilaCoin.Status.AG_CONSOLIDACAO);

                this.pilaCoinService.save(pilaCoin);
            }

            return;
        }

        if (message.getMsg().startsWith("Pila validado por mais da metade dos peers.")) {
            PilaCoin pilaCoin = this.pilaCoinService.findByNonce(message.getNonce());

            if (pilaCoin != null) {
                pilaCoin.setStatus(PilaCoin.Status.AG_BLOCO);

                this.pilaCoinService.save(pilaCoin);
            }

            return;
        }
    }
}
