package com.binarybeast.cipherstore.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Service;

import com.binarybeast.cipherstore.crypto.CryptoFunction;

@Service
public class CryptoService {
    private static final String KEY_STORE_SALT = new String(new byte[] {
        61, 61, 67, 97, 114, 100,
        36, 35, 83, 116, 111, 114,
        101, 94, 95, 102, 101, 97,
        116, 117, 114, 101, 64, 79,
        80, 71, 45, 50, 49, 55,
        61, 61
    });

    public String encrypt(String plainText, String cipherKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String cipherText = CryptoFunction.encrypt(plainText, cipherKey, KEY_STORE_SALT);
        return cipherText;
    }

    public String decrypt(String cipherText, String cipherKey) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        String plainText = CryptoFunction.decrypt(cipherText, cipherKey, KEY_STORE_SALT);
        return plainText;
    }
}
