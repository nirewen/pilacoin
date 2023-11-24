package br.ufsm.csi.tapw.pilacoin.util;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Random;

public class CryptoUtil {
    private static final Random random = new Random(System.currentTimeMillis());

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
        return CryptoUtil.digest(json.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static byte[] digest(byte[] byteArray) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        return md.digest(byteArray);
    }

    public static BigInteger hash(String json) {
        return new BigInteger(CryptoUtil.digest(json)).abs();
    }

    public static BigInteger hash(byte[] byteArray) {
        return new BigInteger(CryptoUtil.digest(byteArray)).abs();
    }

    public static String getRandomNonce() {
        byte[] byteArray = new byte[256 / 8];

        CryptoUtil.random.nextBytes(byteArray);

        return CryptoUtil.hash(byteArray).toString();
    }
}
