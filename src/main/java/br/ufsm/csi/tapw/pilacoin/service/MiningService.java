package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

@Service
public class MiningService implements Runnable, Observer<Difficulty> {
    private final QueueService queueService;
    private final PilaCoinService pilaCoinService;
    private final SharedUtil sharedUtil;
    private Difficulty difficulty;

    public MiningService(QueueService queueService, PilaCoinService pilaCoinService, SharedUtil sharedUtil) {
        this.queueService = queueService;
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
    }

    @Override
    @SneakyThrows
    public void run() {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        PilaCoinJson pilaCoin = PilaCoinJson.builder()
            .chaveCriador(this.sharedUtil.getPublicKey().toString().getBytes(StandardCharsets.UTF_8))
            .nomeCriador(this.sharedUtil.getProperties().USERNAME)
            .build();
        int count = 0;
        Random random = new Random();
        System.out.println("Minerando...");

        while (true) {
            byte[] byteArray = new byte[256 / 8];

            random.nextBytes(byteArray);
            pilaCoin.setNonce(new BigInteger(md.digest(byteArray)).abs().toString());
            pilaCoin.setDataCriacao(new Date(System.currentTimeMillis()));

            String json = JacksonUtil.toString(pilaCoin);

            count++;

            if (CryptoUtil.compareHash(json, this.difficulty.getDificuldade())) {
                System.out.printf("Encontrado Pila depois de %d tentativas%n", count);

                this.pilaCoinService.save(pilaCoin);

                this.queueService.publishPilaCoin(pilaCoin);

                count = 0;
            }
        }
    }

    @Override
    public void update(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
