package br.ufsm.csi.tapw.pilacoin.types;

import br.ufsm.csi.tapw.pilacoin.util.SettingsManager;

public interface SettingsUpdateListener {
    void onUpdateSettings(SettingsManager subject);
}
