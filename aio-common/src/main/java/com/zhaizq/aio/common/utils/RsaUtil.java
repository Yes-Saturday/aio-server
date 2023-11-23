package com.zhaizq.aio.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author RSA签名,加解密处理核心文件,注意:密钥长度1024
 */
public class RsaUtil {
    // 签名算法
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    // 加密算法RSA
    public static final String KEY_ALGORITHM = "RSA";
    // RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static KeyFactory keyFactory;

    private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        return keyFactory == null ? keyFactory = KeyFactory.getInstance(KEY_ALGORITHM) : keyFactory;
    }

    private static PublicKey generatePublic(byte[] key) {
        try {
            return getKeyFactory().generatePublic(new X509EncodedKeySpec(key));
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    private static PrivateKey generatePrivate(byte[] key) {
        try {
            return getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(key));
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    public static byte[] sign(byte[] data, byte[] privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(generatePrivate(privateKey));
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    public static boolean verify(byte[] data, byte[] sign, byte[] publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(generatePublic(publicKey));
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    public static byte[] encryptByPublicKey(byte[] data, byte[] key) {
        return rsaAlgorithm(data, generatePublic(key), Cipher.ENCRYPT_MODE, MAX_ENCRYPT_BLOCK);
    }

    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) {
        return rsaAlgorithm(data, generatePrivate(key), Cipher.ENCRYPT_MODE, MAX_ENCRYPT_BLOCK);
    }

    public static byte[] decryptByPublicKey(byte[] data, byte[] key) {
        return rsaAlgorithm(data, generatePublic(key), Cipher.DECRYPT_MODE, MAX_DECRYPT_BLOCK);
    }

    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) {
        return rsaAlgorithm(data, generatePrivate(key), Cipher.DECRYPT_MODE, MAX_DECRYPT_BLOCK);
    }

    public static String sign(String data, String privateKey) {
        byte[] sign = RsaUtil.sign(data.getBytes(), Base64.getDecoder().decode(privateKey));
        return Base64.getEncoder().encodeToString(sign);
    }

    public static boolean verify(String data, String sign, String publicKey) {
        return RsaUtil.verify(data.getBytes(), Base64.getDecoder().decode(sign), Base64.getDecoder().decode(publicKey));
    }

    public static String encryptByPublicKey(String data, String key) {
        byte[] bytes = RsaUtil.encryptByPublicKey(data.getBytes(), Base64.getDecoder().decode(key));
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String encryptByPrivateKey(String data, String key) {
        byte[] bytes = RsaUtil.encryptByPrivateKey(data.getBytes(), Base64.getDecoder().decode(key));
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decryptByPublicKey(String data, String key) {
        byte[] bytes = RsaUtil.decryptByPublicKey(Base64.getDecoder().decode(data), Base64.getDecoder().decode(key));
        return new String(bytes);
    }

    public static String decryptByPrivateKey(String data, String key) {
        byte[] bytes = RsaUtil.decryptByPrivateKey(Base64.getDecoder().decode(data), Base64.getDecoder().decode(key));
        return new String(bytes);
    }

    /**
     * 密文算法
     */
    private static byte[] rsaAlgorithm(byte[] data, Key key, int mode, int block) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Cipher cipher = Cipher.getInstance(getKeyFactory().getAlgorithm());
            cipher.init(mode, key);
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > block) {
                    cache = cipher.doFinal(data, offSet, block);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * block;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    /**
     * 生成秘钥对
     */
    public static Keys<byte[]> genKeyPair() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            return new Keys<>(keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Keys<String> genKeyPairString() {
        Keys<byte[]> keys = RsaUtil.genKeyPair();
        return new Keys<>(Base64.getEncoder().encodeToString(keys.getPublicKey()), Base64.getEncoder().encodeToString(keys.getPrivateKey()));
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class Keys<T> {
        private T publicKey;
        private T privateKey;
    }
}