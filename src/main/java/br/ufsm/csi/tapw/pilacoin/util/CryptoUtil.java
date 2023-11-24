package br.ufsm.csi.tapw.pilacoin.util;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;

public class CryptoUtil {
    @SneakyThrows
    public static boolean compareHash(String json, BigInteger hash) {
        BigInteger calculatedHash = CryptoUtil.hash(json);

        return calculatedHash.compareTo(hash) < 0;
    }

    @SneakyThrows
    public static byte[] sign(String json, PrivateKey privateKey) {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        byte[] hashByteArr = CryptoUtil.digest(json);

        encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return encryptCipher.doFinal(hashByteArr);
    }

    @SneakyThrows
    public static byte[] digest(String json) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        return md.digest(json.getBytes(StandardCharsets.UTF_8));
    }

    public static BigInteger hash(String json) {
        return new BigInteger(CryptoUtil.digest(json)).abs();
    }
}
