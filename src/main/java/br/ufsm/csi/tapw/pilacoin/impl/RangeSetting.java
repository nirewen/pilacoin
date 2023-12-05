package br.ufsm.csi.tapw.pilacoin.impl;

import br.ufsm.csi.tapw.pilacoin.types.AbstractSetting;
import br.ufsm.csi.tapw.pilacoin.types.Range;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RangeSetting extends AbstractSetting<Range> {
    public RangeSetting(String name, Integer value, Integer min, Integer max) {
        super(name, new Range(value, min, max));
    }

    public void setValue(Integer value) {
        if (value < this.value.getMin() || value > this.getValue().getMax()) {
            throw new IllegalArgumentException("Value must be between " + this.getValue().getMin() + " and " + this.getValue().getMax() + ".");
        }

        this.value.setValue(value);
    }
}
