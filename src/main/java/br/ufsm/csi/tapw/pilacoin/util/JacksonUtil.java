package br.ufsm.csi.tapw.pilacoin.util;

import br.ufsm.csi.tapw.pilacoin.model.Difficulty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;

public class JacksonUtil {
    @SneakyThrows
    public static <T> T convert(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addDeserializer(Difficulty.class, new DifficultyDeserializer());
        mapper.registerModule(module);

        try {
            return mapper.readValue(jsonString, clazz);
        } catch(Exception e) {
            return null;
        }
    }

    @SneakyThrows
    public static <T> String toString(T object) {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(object);
    }
}
