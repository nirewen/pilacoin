package br.ufsm.csi.tapw.pilacoin.impl;

import br.ufsm.csi.tapw.pilacoin.model.internal.AbstractSetting;

public class BooleanSetting extends AbstractSetting<Boolean> {
    public BooleanSetting(String name, boolean value) {
        super(name, value);
    }
}
