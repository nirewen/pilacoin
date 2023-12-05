package br.ufsm.csi.tapw.pilacoin.model;

import br.ufsm.csi.tapw.pilacoin.util.jackson.DifficultyDeserializer;
import br.ufsm.csi.tapw.pilacoin.util.jackson.DifficultySerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Difficulty {
    Date inicio;
    Date validadeFinal;
    
    @JsonDeserialize(using = DifficultyDeserializer.class)
    @JsonSerialize(using = DifficultySerializer.class)
    BigInteger dificuldade;

}