package br.ufsm.csi.tapw.pilacoin.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Properties {
    @Value("${pilacoin.username}")
    public String USERNAME;
}
