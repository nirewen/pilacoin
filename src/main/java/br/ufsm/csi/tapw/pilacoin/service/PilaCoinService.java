package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.tapw.pilacoin.repository.PilaCoinRepository;
import org.springframework.stereotype.Service;

@Service
public class PilaCoinService {
    private final PilaCoinRepository pilaCoinRepository;

    public PilaCoinService(PilaCoinRepository pilaCoinRepository) {
        this.pilaCoinRepository = pilaCoinRepository;
    }

    public void save(PilaCoinJson pilaCoinJSON) {
        PilaCoin pilaCoin = PilaCoin.builder()
            .chaveCriador(pilaCoinJSON.getChaveCriador())
            .nomeCriador(pilaCoinJSON.getNomeCriador())
            .dataCriacao(pilaCoinJSON.getDataCriacao())
            .nonce(pilaCoinJSON.getNonce())
            .build();

        this.pilaCoinRepository.save(pilaCoin);
    }
}
