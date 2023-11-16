package br.ufsm.csi.tapw.pilacoin.util;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Component
public class SharedUtil {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @SneakyThrows
    @PostConstruct
    private void loadKeypair() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);

        this.publicKey = keyPairGenerator.generateKeyPair().getPublic();
        this.privateKey = keyPairGenerator.generateKeyPair().getPrivate();
    }
}
