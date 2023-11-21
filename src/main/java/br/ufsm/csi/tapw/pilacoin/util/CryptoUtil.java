package br.ufsm.csi.tapw.pilacoin.util;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;

public class CryptoUtil {
    @SneakyThrows
    public static BigInteger hash(String json) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        return new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8))).abs();
    }

    @SneakyThrows
    public static boolean compareHash(String json, BigInteger hash) {
        BigInteger calculatedHash = CryptoUtil.hash(json);

        return calculatedHash.compareTo(hash) < 0;
    }

    @SneakyThrows
    public static byte[] sign(String json, PrivateKey privateKey) {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] hashByteArr = hash(json).toString().getBytes(StandardCharsets.UTF_8);

        return encryptCipher.doFinal(hashByteArr);
    }
}
