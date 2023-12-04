package br.ufsm.csi.tapw.pilacoin.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Range {
    private Integer value;
    private Integer min;
    private Integer max;
}
