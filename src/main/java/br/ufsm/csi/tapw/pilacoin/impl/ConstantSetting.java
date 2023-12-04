package br.ufsm.csi.tapw.pilacoin.impl;

import br.ufsm.csi.tapw.pilacoin.types.AbstractSetting;

public class ConstantSetting extends AbstractSetting<Integer> {
    public ConstantSetting(String name, Integer value) {
        super(name, value);
    }
}
