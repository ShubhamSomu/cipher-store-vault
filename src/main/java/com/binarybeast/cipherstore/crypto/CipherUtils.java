package com.binarybeast.cipherstore.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.ArrayUtils;

public class CipherUtils {

    public static Cipher newCipher(final String algorithm) {
        try {
            return Cipher.getInstance(algorithm);
        } catch (NoSuchPaddingException nspe) {
            throw new IllegalArgumentException(nspe);
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException(nsae);
        }
    }

    public static void initCipher(final Cipher cipher, final int mode, final SecretKey secretKey, final AlgorithmParameterSpec parameterSpec) {
        try {
            cipher.init(mode, secretKey, parameterSpec);
        } catch (InvalidKeyException ike) {
            throw new IllegalArgumentException(ike);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new IllegalStateException(iape);
        }
    }

    public static byte[] doFinal(final Cipher cipher, final byte[] input) {
        try {
            return cipher.doFinal(input);
        } catch (IllegalBlockSizeException ibse) {
            throw new IllegalStateException(ibse);
        } catch (BadPaddingException bpe) {
            throw new IllegalStateException(bpe);
        }
    }

    public static ByteBuffer ivConcatCipherText(byte[] iv, byte[] cipherBytes) {
        if (ArrayUtils.isEmpty(iv) || ArrayUtils.isEmpty(cipherBytes)) {
            throw new IllegalArgumentException("IV and cipher Bytes can't be null");
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherBytes);
        return byteBuffer;
    }
}
