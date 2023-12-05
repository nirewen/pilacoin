package br.ufsm.csi.tapw.pilacoin.model;

import br.ufsm.csi.tapw.pilacoin.util.DifficultyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(using = DifficultyDeserializer.class)
    BigInteger dificuldade;
    Date inicio;
    Date validadeFinal;
}