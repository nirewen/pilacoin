package br.ufsm.csi.tapw.pilacoin.util.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JacksonUtil {
    @SneakyThrows
    public static <T> T convert(String jsonString, Object valueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            if (valueType instanceof Class) {
                return mapper.readValue(jsonString, (Class<T>) valueType);
            } else if (valueType instanceof TypeReference<?>) {
                return mapper.readValue(jsonString, (TypeReference<T>) valueType);
            } else {
                throw new IllegalArgumentException("Unsupported valueType");
            }
        } catch (Exception e) {
            return null;
        }
    }

    @SneakyThrows
    public static <T> String toString(T object) {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(object);
    }
}
