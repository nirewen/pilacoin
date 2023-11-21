package br.ufsm.csi.tapw.pilacoin.constants;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class Properties {
    @Value("${pilacoin.username}")
    public String username;
}
