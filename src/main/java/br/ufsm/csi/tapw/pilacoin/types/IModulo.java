package br.ufsm.csi.tapw.pilacoin.types;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import br.ufsm.csi.tapw.pilacoin.model.Modulo;

public abstract class IModulo implements Observer<Difficulty> {
    public Modulo modulo;

    public String getNome() {
        return this.getClass().getSimpleName().replaceAll("Service", "");
    }

    public void register(Modulo modulo) {
        this.modulo = modulo;
    }
}
