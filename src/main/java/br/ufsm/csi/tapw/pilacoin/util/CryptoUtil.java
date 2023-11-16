package br.ufsm.csi.tapw.pilacoin.util;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class CryptoUtil {
    @SneakyThrows
    public static boolean compareHash(String json, BigInteger hash) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger calculatedHash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8))).abs();

        return calculatedHash.compareTo(hash) < 0;
    }
}
