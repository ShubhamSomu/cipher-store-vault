package com.binarybeast.cipherstore.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.binarybeast.cipherstore.secret.Secret;
import com.binarybeast.cipherstore.service.KeyService;

@RestController
public class CipherStoreController {
    private final KeyService keyService;

    public CipherStoreController(KeyService keyService) {
        this.keyService = Objects.requireNonNull(keyService, "keyService");
    }

    @PostMapping("v1/cipher-store/store")
    public void storeKeysViaKV(@Valid @RequestBody Secret secret) throws Exception {
        keyService.storeKeys(secret, "v1");
    }

    /**
     * This will be at balance-api
     * @throws Exception
     */
    @GetMapping("v1/cipher-store/fetch")
    public Secret fetchKeysViaKV() throws Exception {
        return keyService.fetchKeys("v1");
    }

    // v2 is for transit engne in this case MEK is null.
    @PostMapping("v2/cipher-store/store")
    public void storeKeysViaTransit(@RequestBody Secret secret) throws Exception {
        secret.setMek(StringUtils.EMPTY);
        keyService.storeKeys(secret, "v2");
    }

    @GetMapping("v2/cipher-store/fetch")
    public Secret fetchKeysViaTransit() throws Exception {
        return keyService.fetchKeys("v2");
    }
}
