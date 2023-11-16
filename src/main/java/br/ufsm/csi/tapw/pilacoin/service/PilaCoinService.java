package br.ufsm.csi.tapw.pilacoin.service;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.csi.tapw.pilacoin.repository.PilaCoinRepository;
import org.springframework.stereotype.Service;

@Service
public class PilaCoinService {
    private final PilaCoinRepository pilaCoinRepository;

    public PilaCoinService(PilaCoinRepository pilaCoinRepository) {
        this.pilaCoinRepository = pilaCoinRepository;
    }

    public void save(PilaCoin pilaCoin) {
        this.pilaCoinRepository.save(pilaCoin);
    }
}
