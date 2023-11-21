package br.ufsm.csi.tapw.pilacoin.util;

import br.ufsm.csi.tapw.pilacoin.constants.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Data
@Component
public class SharedUtil {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Properties properties;

    public SharedUtil(Properties properties) {
        this.properties = properties;
    }

    @SneakyThrows
    @PostConstruct
    private void loadKeypair() {
        Path privateKeyPath = this.properties.getHomePath().resolve("private.key");
        Path publicKeyPath = this.properties.getHomePath().resolve("public.key");

        if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyPath);
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyPath);

            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);

            this.privateKey = kf.generatePrivate(privateSpec);
            this.publicKey = kf.generatePublic(publicSpec);
        } else {
            Files.createDirectories(this.properties.getHomePath());

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();

            Files.write(privateKeyPath, this.privateKey.getEncoded());
            Files.write(publicKeyPath, this.publicKey.getEncoded());
        }

    }
}
