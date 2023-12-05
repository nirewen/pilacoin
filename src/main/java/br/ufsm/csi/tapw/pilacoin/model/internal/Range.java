package br.ufsm.csi.tapw.pilacoin.model.internal;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class Range extends LinkedHashMap<String, Integer> {
    private Integer value;
    private Integer min;
    private Integer max;

    public Range(Integer value, Integer min, Integer max) {
        this.put("value", value);
        this.put("min", min);
        this.put("max", max);
    }
}
