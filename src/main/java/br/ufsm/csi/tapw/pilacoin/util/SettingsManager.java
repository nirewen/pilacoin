package br.ufsm.csi.tapw.pilacoin.util;

import br.ufsm.csi.tapw.pilacoin.types.AbstractSetting;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
        return (AbstractSetting<T>) this.settings
            .stream()
            .filter(s -> s.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    public String getString(String name) {
        AbstractSetting<String> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        return setting.getValue();
    }

    public Boolean getBoolean(String name) {
        AbstractSetting<Boolean> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        return setting.getValue();
    }

    public Integer getInteger(String name) {
        AbstractSetting<Integer> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        return setting.getValue();
    }

    public Integer getRangeValue(String name) {
        AbstractSetting<LinkedHashMap<String, Integer>> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        return setting.getValue().get("value");
    }

    public void setString(String name, String value) {
        AbstractSetting<String> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        setting.setValue(value);
    }

    public void setBoolean(String name, Boolean value) {
        AbstractSetting<Boolean> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        setting.setValue(value);
    }

    public void setInteger(String name, Integer value) {
        AbstractSetting<Integer> setting = this.getSetting(name);

        if (setting == null) {
            throw new RuntimeException("Setting not found");
        }

        setting.setValue(value);
    }
}
