package br.ufsm.csi.tapw.pilacoin.types.observer;

import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;

public interface SettingsUpdateListener {
    void onUpdateSettings(SettingsManager subject);
}
