package br.ufsm.csi.tapw.pilacoin.repository;

import br.ufsm.csi.tapw.pilacoin.model.PilaCoin;
import org.springframework.data.repository.Repository;

public interface PilaCoinRepository extends Repository<PilaCoin, Long> {
    void save(PilaCoin pilaCoin);
}
