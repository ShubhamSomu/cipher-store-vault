package com.binarybeast.cipherstore.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import javax.print.DocFlavor.STRING;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.vault.client.VaultEndpoint;

import com.binarybeast.cipherstore.secret.Secret;

import lombok.extern.slf4j.Slf4j;

@Service

public class KeyStoreService {
    private static final Logger log = LoggerFactory.getLogger(KeyStoreService.class);
    private final CryptoService cryptoService;
    private final VaultService vaultService;

    public KeyStoreService(CryptoService cryptoService, VaultService vaultService) {
        this.cryptoService = Objects.requireNonNull(cryptoService, "cryptoService");
        this.vaultService = Objects.requireNonNull(vaultService, "vaultService");
    }

    public void storeKeys(String dek, String mek) throws Exception {
        //todo shubham remove logs
        log.debug("DEK: {} ", dek);
        log.debug("MEK: {}", mek);

        if (StringUtils.isBlank(dek) || StringUtils.isBlank(mek)) {
            throw new IllegalArgumentException("MEK or DEK can't be null for storage");
        }

        String cipherDek = cryptoService.encrypt(dek, mek);

        Secret secret = new Secret();
        secret.setDek(cipherDek);
        secret.setMek(mek);

        vaultService.storeInVault(secret);
    }

    public Secret fetchKeys() throws Exception {
        Secret secret = vaultService.fetchFromVault();

        if (ObjectUtils.isEmpty(secret) || StringUtils.isBlank(secret.getMek()) || StringUtils.isBlank(secret.getDek())) {
            log.debug("Secrets are empty at vault");
        }

        String temp = cryptoService.decrypt(secret.getDek(), secret.getMek());

        log.debug("Decrypted DEK: {}", temp);
        secret.setDek(temp);
        return secret;
    }
}
