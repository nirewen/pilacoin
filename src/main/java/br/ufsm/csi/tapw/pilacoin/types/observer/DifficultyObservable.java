package br.ufsm.csi.tapw.pilacoin.types.observer;

import java.util.ArrayList;
import java.util.List;

public interface DifficultyObservable {
    List<DifficultyObserver> observers = new ArrayList<>();
    
    void subscribe(DifficultyObserver observer);

    void unsubscribe(DifficultyObserver observer);
}
