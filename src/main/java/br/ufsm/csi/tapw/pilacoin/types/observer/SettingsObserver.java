package br.ufsm.csi.tapw.pilacoin.types.observer;

import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;

public interface SettingsObserver {
    void updateSettings(SettingsManager subject);
}
