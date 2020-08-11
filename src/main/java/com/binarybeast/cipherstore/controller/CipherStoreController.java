package com.binarybeast.cipherstore.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.binarybeast.cipherstore.secret.Secret;
import com.binarybeast.cipherstore.service.KeyStoreService;

@RestController
public class CipherStoreController {
    private final KeyStoreService keyStoreService;

    public CipherStoreController(KeyStoreService keyStoreService) {
        this.keyStoreService = Objects.requireNonNull(keyStoreService, "keyStoreService");
    }

    @PostMapping("/cipher-store/store")
    public void storeKeys(@Valid @RequestBody Secret secret) throws Exception {
        keyStoreService.storeKeys(secret.getDek(), secret.getMek());
    }

    /**
     * This will be at balance-api
     * @throws Exception
     */
    @GetMapping("/cipher-store/fetch")
    public Secret fetchKeys() throws Exception {
        return keyStoreService.fetchKeys();
    }
}
