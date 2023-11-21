package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.tapw.pilacoin.types.Observer;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

@Service
public class PilaCoinService implements Observer<Difficulty> {
    private final PilaCoinRepository pilaCoinRepository;
    private final SharedUtil sharedUtil;
    private final Random random = new Random();
    private final MessageDigest md = MessageDigest.getInstance("SHA-256");
    private Difficulty difficulty;

    public PilaCoinService(PilaCoinRepository pilaCoinRepository, SharedUtil sharedUtil) throws NoSuchAlgorithmException {
        this.pilaCoinRepository = pilaCoinRepository;
        this.sharedUtil = sharedUtil;
    }

    public PilaCoin save(PilaCoinJson pilaCoinJson) {
        PilaCoin pilaCoin = PilaCoin.builder()
            .chaveCriador(pilaCoinJson.getChaveCriador())
            .nomeCriador(pilaCoinJson.getNomeCriador())
            .nonce(pilaCoinJson.getNonce())
            .dataCriacao(pilaCoinJson.getDataCriacao())
            .status(pilaCoinJson.getStatus())
            .build();

        return this.save(pilaCoin);
    }

    public PilaCoin save(PilaCoin pilaCoin) {
        return this.pilaCoinRepository.save(pilaCoin);
    }

    @Transactional
    public PilaCoinJson generatePilaCoin() {
        PilaCoinJson pilaCoin = PilaCoinJson.builder()
            .chaveCriador(this.sharedUtil.getPublicKey().toString().getBytes(StandardCharsets.UTF_8))
            .nomeCriador(this.sharedUtil.getProperties().USERNAME)
            .status(PilaCoin.Status.AG_VALIDACAO)
            .build();

        byte[] byteArray = new byte[256 / 8];

        this.random.nextBytes(byteArray);
        pilaCoin.setNonce(new BigInteger(md.digest(byteArray)).abs().toString());
        pilaCoin.setDataCriacao(new Date(System.currentTimeMillis()));

        String json = JacksonUtil.toString(pilaCoin);

        if (CryptoUtil.compareHash(json, this.difficulty.getDificuldade())) {
            this.save(pilaCoin);

            return pilaCoin;
        }

        return null;
    }

    public PilaCoin findByNonce(String nonce) {
        return this.pilaCoinRepository.findByNonce(nonce);
    }

    public void changeStatus(PilaCoin pilaCoin, PilaCoin.Status status) {
        pilaCoin.setStatus(status);

        this.pilaCoinRepository.save(pilaCoin);
    }

    @Override
    public void update(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
