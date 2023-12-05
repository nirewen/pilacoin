package br.ufsm.csi.tapw.pilacoin.model.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AbstractSetting<T> {
    protected String kind = this.getClass().getSimpleName().replaceAll("Setting", "").toUpperCase();
    protected String name;
    @Setter
    protected T value;

    public AbstractSetting(String name, T value) {
        this.name = name;
        this.value = value;
    }
}
