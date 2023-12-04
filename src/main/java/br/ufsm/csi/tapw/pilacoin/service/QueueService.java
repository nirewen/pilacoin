package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.exception.EmptyResultException;
import br.ufsm.csi.tapw.pilacoin.model.BlocoValidado;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoinValidado;
import br.ufsm.csi.tapw.pilacoin.model.Transferencia;
import br.ufsm.csi.tapw.pilacoin.model.json.BlocoJson;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.model.json.QueryJson;
import br.ufsm.csi.tapw.pilacoin.model.json.QueryResponseJson;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.pila.minerado}")
    private String PILA_MINERADO;
    @Value("${queue.pila.validado}")
    private String PILA_VALIDADO;
    @Value("${queue.bloco.descobre}")
    private String BLOCO_DESCOBERTO;
    @Value("${queue.bloco.minerado}")
    private String BLOCO_MINERADO;
    @Value("${queue.bloco.validado}")
    private String BLOCO_VALIDADO;
    @Value("${queue.pila.transferir}")
    private String TRANSFERIR_PILA;

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @SneakyThrows
    public QueryResponseJson requestQuery(QueryJson queryJson) {
        String json = JacksonUtil.toString(queryJson);
        this.publishToQueue("query", json);

        int tries = 0;

        while (tries++ != 10) {
            String responseJson = this.receiveFromQueue(queryJson.getNomeUsuario() + "-query");
            QueryResponseJson response = JacksonUtil.convert(responseJson, QueryResponseJson.class);

            if (response == null) {
                Thread.sleep(500);

                continue;
            }

            if (response.getIdQuery().equals(queryJson.getIdQuery())) {
                return response;
            }
        }

        throw new EmptyResultException();
    }

    public void publishPilaCoinMinerado(PilaCoinJson pilaCoinJson) {
        this.publishToQueue(PILA_MINERADO, JacksonUtil.toString(pilaCoinJson));
    }

    public void publishPilaCoinValidado(PilaCoinValidado pilaCoinValidado) {
        this.publishToQueue(PILA_VALIDADO, JacksonUtil.toString(pilaCoinValidado));
    }

    public void publishBlocoDescoberto(BlocoJson blocoJson) {
        this.publishToQueue(BLOCO_DESCOBERTO, JacksonUtil.toString(blocoJson));
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

    private String receiveFromQueue(String queue) {
        return (String) this.rabbitTemplate.receiveAndConvert(queue);
    }
}
