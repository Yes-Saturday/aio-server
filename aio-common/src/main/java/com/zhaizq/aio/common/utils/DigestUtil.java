package com.zhaizq.aio.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DigestUtil {
    public static String md5AsHex(String str) {
        return md5AsHex(str.getBytes());
    }

    public static String md5AsHex(byte[] bytes) {
        return encryptAsHex(bytes, "MD5");
    }

    public static String sha256AsHex(String str) {
        return sha256AsHex(str.getBytes());
    }

    public static String sha256AsHex(byte[] bytes) {
        return encryptAsHex(bytes, "SHA-256");
    }

    public static String asHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes)
            result.append((aByte & 0xff) < 16 ? "0" : "").append(Integer.toHexString((aByte & 0xff)));
        return result.toString();
    }

    public static byte[] encrypt(byte[] bytes, String enc) {
        try {
            MessageDigest md = MessageDigest.getInstance(enc);
            md.update(bytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptAsHex(byte[] bytes, String enc) {
        return asHex(encrypt(bytes, enc));
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] base64Decode(String enc) {
        return Base64.getDecoder().decode(enc);
    }
}