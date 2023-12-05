package br.ufsm.csi.tapw.pilacoin.types.observer;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;

public interface DifficultyObserver {
    void updateDifficulty(Difficulty subject);
}
