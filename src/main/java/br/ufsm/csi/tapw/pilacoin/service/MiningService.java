package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class MiningService implements Runnable {
    private final QueueService queueService;
    private final PilaCoinService pilaCoinService;

    public MiningService(QueueService queueService, PilaCoinService pilaCoinService) {
        this.queueService = queueService;
        this.pilaCoinService = pilaCoinService;
    }

    @Override
    @SneakyThrows
    public void run() {
        int count = 0;
        System.out.println("Minerando...");

        while (true) {
            count++;

            PilaCoinJson pilaCoin = this.pilaCoinService.generatePilaCoin();

            if (pilaCoin != null) {
                System.out.printf("Encontrado Pila depois de %d tentativas%n", count);

                this.queueService.publishPilaCoin(pilaCoin);

                count = 0;
            }
        }
    }
}
