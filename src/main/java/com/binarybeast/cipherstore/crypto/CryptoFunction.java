package com.binarybeast.cipherstore.crypto;

import static com.binarybeast.cipherstore.crypto.CipherUtils.doFinal;
import static com.binarybeast.cipherstore.crypto.CipherUtils.initCipher;
import static com.binarybeast.cipherstore.crypto.CipherUtils.newCipher;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoFunction {

    private static final int IV_BIT_LENGTH = 96;
    private static final int IV_BIT_LENGTH_IN_BYTES = IV_BIT_LENGTH / 8;
    private static final int AUTH_TAG_BIT_LENGTH = 128;
    private static final int AES_KEY_LENGTH = 256;
    private static final int HASH_ITERATIONS = 65536;
    private static final String ALGORITHM = "AES";

    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";

    private static final Cipher ENCRYPTOR = newCipher(AES_GCM_ALGORITHM);
    private static final Cipher DECRYPTOR = newCipher(AES_GCM_ALGORITHM);
    private static final String AES_MODE_FACTORY = "PBKDF2WithHmacSHA256";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String encrypt(String plainText, String secret, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {

        if (Objects.isNull(plainText)) {
            return null;
        }

        synchronized (ENCRYPTOR) {
            byte[] valueBytes = plainText.getBytes(StandardCharsets.UTF_8);
            final SecretKey secretKey = createSecretKey(secret, salt);
            final byte[] iv = getIV();

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(AUTH_TAG_BIT_LENGTH, iv);

            initCipher(ENCRYPTOR, Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] cipherBytes = doFinal(ENCRYPTOR, valueBytes);

            return Base64.getEncoder().encodeToString(CipherUtils.ivConcatCipherText(iv, cipherBytes).array());
        }
    }

    public static String decrypt(String cipherText, String secret, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        if (Objects.isNull(cipherText)) {
            return null;
        }
        synchronized (DECRYPTOR) {
            byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
            SecretKey secretKey = createSecretKey(secret, salt);

            final byte[] iv = Arrays.copyOfRange(cipherBytes, 0, IV_BIT_LENGTH_IN_BYTES);
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(AUTH_TAG_BIT_LENGTH, cipherBytes, 0, iv.length);

            Cipher cipher = createCipher(secretKey, Cipher.DECRYPT_MODE, gcmParameterSpec);
            return new String(cipher.doFinal(cipherBytes, IV_BIT_LENGTH_IN_BYTES, cipherBytes.length - IV_BIT_LENGTH_IN_BYTES), StandardCharsets.UTF_8);
        }
    }

    public static Cipher createCipher(SecretKey secretKey, int type, final AlgorithmParameterSpec parameterSpec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
        cipher.init(type, secretKey, parameterSpec);
        return cipher;
    }

    private static SecretKeySpec createSecretKey(String secret, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(AES_MODE_FACTORY);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), HASH_ITERATIONS, AES_KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        return secretKey;
    }

    private static byte[] getIV() {
        byte[] iv = new byte[IV_BIT_LENGTH_IN_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }
}
