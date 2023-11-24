package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.tapw.pilacoin.util.CryptoUtil;
import br.ufsm.csi.tapw.pilacoin.util.JacksonUtil;
import br.ufsm.csi.tapw.pilacoin.util.SharedUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class PilaCoinService {
    private final PilaCoinRepository pilaCoinRepository;
    private final SharedUtil sharedUtil;

    public PilaCoinService(PilaCoinRepository pilaCoinRepository, SharedUtil sharedUtil) {
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
    public PilaCoinJson generatePilaCoin(Difficulty difficulty) {
        PilaCoinJson pilaCoin = PilaCoinJson.builder()
            .chaveCriador(this.sharedUtil.getPublicKey().toString().getBytes(StandardCharsets.UTF_8))
            .nomeCriador(this.sharedUtil.getProperties().getUsername())
            .status(PilaCoin.Status.AG_VALIDACAO)
            .build();

        pilaCoin.setNonce(CryptoUtil.getRandomNonce());
        pilaCoin.setDataCriacao(new Date(System.currentTimeMillis()));

        String json = JacksonUtil.toString(pilaCoin);

        if (CryptoUtil.compareHash(json, difficulty.getDificuldade())) {
            this.save(pilaCoin);

            return pilaCoin;
        }

        return null;
    }

    public PilaCoin findByNonce(String nonce) {
        return this.pilaCoinRepository.findByNonce(nonce);
    }

    public void changeStatus(PilaCoin pilaCoin, PilaCoin.Status status) {
        if (pilaCoin == null || status == null) {
            return;
        }

        pilaCoin.setStatus(status);

        this.pilaCoinRepository.save(pilaCoin);
    }
}
