package br.ufsm.csi.tapw.pilacoin.impl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RangeSetting extends ConstantSetting {
    private Integer min;
    private Integer max;

    public RangeSetting(String name, Integer value, Integer min, Integer max) {
        super(name, value);

        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public void setValue(Integer value) {
        if (value < this.min || value > this.max) {
            throw new IllegalArgumentException("Value must be between " + this.min + " and " + this.max);
        }

        this.value = value;
    }
}
