package br.ufsm.csi.tapw.pilacoin.model.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Range extends LinkedHashMap<String, Integer> {
    private Integer value;
    private Integer min;
    private Integer max;
}
