package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.minerado}")
    private String filaMinerado;

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPilaCoin(PilaCoinJson pilaCoinJson) {
        this.rabbitTemplate.convertAndSend(filaMinerado, JacksonUtil.toString(pilaCoinJson));
    }

    @RabbitListener(queues = "${pilacoin.username}")
    public void receiveResponse(@Payload String response) {
        System.out.println(response);
    }
}
