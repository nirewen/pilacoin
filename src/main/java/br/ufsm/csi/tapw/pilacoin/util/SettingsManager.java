package br.ufsm.csi.tapw.pilacoin.util;

import br.ufsm.csi.tapw.pilacoin.model.internal.AbstractSetting;
import br.ufsm.csi.tapw.pilacoin.model.internal.Range;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class SettingsManager {
    private final List<AbstractSetting<?>> settings = new ArrayList<>();

    public SettingsManager(AbstractSetting<?>... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public SettingsManager(List<AbstractSetting<?>> settings) {
        this.settings.addAll(settings);
    }

    public <T> AbstractSetting<T> getSetting(String name) {
        AbstractSetting<T> setting = (AbstractSetting<T>) this.settings
            .stream()
            .filter(s -> s.getName().equals(name))
            .findFirst()
            .orElse(null);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        return setting;
    }

    public String getString(String name) {
        AbstractSetting<String> setting = this.getSetting(name);

        return setting.getValue();
    }

    public Boolean getBoolean(String name) {
        AbstractSetting<Boolean> setting = this.getSetting(name);

        return setting.getValue();
    }

    public Integer getInteger(String name) {
        AbstractSetting<Integer> setting = this.getSetting(name);

        return setting.getValue();
    }

    public Integer getRangeValue(String name) {
        AbstractSetting<Range> setting = this.getSetting(name);

        return setting.getValue().get("value");
    }

    public void setString(String name, String value) {
        AbstractSetting<String> setting = this.getSetting(name);

        setting.setValue(value);
    }

    public void setBoolean(String name, Boolean value) {
        AbstractSetting<Boolean> setting = this.getSetting(name);

        setting.setValue(value);
    }

    public void setInteger(String name, Integer value) {
        AbstractSetting<Integer> setting = this.getSetting(name);

        setting.setValue(value);
    }
}
