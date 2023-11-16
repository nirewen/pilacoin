package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.constants.Properties;
import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

@Service
public class MiningService implements Runnable, Observer<Difficulty> {
    private final PilaCoinService pilaCoinService;

    private final SharedUtil sharedUtil;
    private Difficulty difficulty;

    public MiningService(PilaCoinService pilaCoinService, SharedUtil sharedUtil) {
        this.pilaCoinService = pilaCoinService;
        this.sharedUtil = sharedUtil;
    }

    @Override
    @SneakyThrows
    public void run() {
        BigInteger hash;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String json = "";
        PilaCoin pilaCoin = PilaCoin.builder()
                .chaveCriador(this.sharedUtil.getPublicKey().toString().getBytes(StandardCharsets.UTF_8))
                .nomeCriador(Properties.USERNAME)
                .build();
        int count = 0;
        Random random = new Random();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        System.out.println("Minerando...");

        while (true) {
            byte[] byteArray = new byte[256 / 8];

            random.nextBytes(byteArray);
            pilaCoin.setNonce(new BigInteger(md.digest(byteArray)).abs().toString());
            pilaCoin.setDataCriacao(new Date(System.currentTimeMillis()));

            json = ow.writeValueAsString(pilaCoin);
            hash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8))).abs();

            count++;

            if (hash.compareTo(this.difficulty.getDificuldade()) < 0) {
                System.out.println(json);
                this.pilaCoinService.save(pilaCoin);
                count = 0;
            }
        }
    }

    @Override
    public void update(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
