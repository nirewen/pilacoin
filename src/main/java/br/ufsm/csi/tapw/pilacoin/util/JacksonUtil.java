package br.ufsm.csi.tapw.pilacoin.util;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;

public class JacksonUtil {
    @SneakyThrows
    public static <T> T convert(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addDeserializer(Difficulty.class, new DifficultyDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(jsonString, clazz);
    }
}
