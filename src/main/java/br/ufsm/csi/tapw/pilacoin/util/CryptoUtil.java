package br.ufsm.csi.tapw.pilacoin.util;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class CryptoUtil {
    private static final MessageDigest md;
    private static final Cipher encryptCipher;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
            encryptCipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static boolean compareHash(String json, BigInteger hash) {
        BigInteger calculatedHash = CryptoUtil.hash(json);

        return calculatedHash.compareTo(hash) < 0;
    }

    @SneakyThrows
    public static byte[] sign(String json, PrivateKey privateKey) {
        encryptCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] hashByteArr = CryptoUtil.digest(json);

        return encryptCipher.doFinal(hashByteArr);
    }

    @SneakyThrows
    public static byte[] digest(String json) {
        return md.digest(json.getBytes(StandardCharsets.UTF_8));
    }

    public static BigInteger hash(String json) {
        return new BigInteger(CryptoUtil.digest(json)).abs();
    }
}
