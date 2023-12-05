package br.ufsm.csi.tapw.pilacoin.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;

public class DifficultySerializer extends JsonSerializer<BigInteger> {
    public void serialize(BigInteger bigInteger, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String valueAsString = bigInteger.toString(16);

        jsonGenerator.writeString(valueAsString);
    }
}
