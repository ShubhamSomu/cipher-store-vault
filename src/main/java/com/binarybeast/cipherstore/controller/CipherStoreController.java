package com.binarybeast.cipherstore.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.binarybeast.cipherstore.dao.CardStore;
import com.binarybeast.cipherstore.dao.Secret;
import com.binarybeast.cipherstore.service.CardStoreService;
import com.binarybeast.cipherstore.service.KeyService;
import com.binarybeast.cipherstore.types.Versions;

import lombok.NonNull;

@RestController
public class CipherStoreController {
    private final KeyService keyService;
    private final CardStoreService cardStoreService;

    public CipherStoreController(KeyService keyService, CardStoreService cardStoreService) {
        this.keyService = Objects.requireNonNull(keyService, "keyService");
        this.cardStoreService = Objects.requireNonNull(cardStoreService, "cardStoreService");
    }

    @PostMapping("v1/cipher-store/store")
    public void storeKeysViaKV(@Valid @RequestBody Secret secret) throws Exception {
        keyService.storeKeys(secret, Versions.V1);
    }

    /**
     * This will be at balance-api
     * @throws Exception
     */
    @GetMapping("v1/cipher-store/fetch")
    public Secret fetchKeysViaKV() throws Exception {
        return keyService.fetchKeys(Versions.V1);
    }


    @PostMapping("v1/save-card")
    public void saveCardViaKV(@Valid @RequestBody CardStore cardStore) throws Exception {
        cardStoreService.storeCard(cardStore, Versions.V1);
    }

    @GetMapping("v1/fetch-card")
    public CardStore fetchCardViaKV() throws Exception {
        return cardStoreService.fetchCard(Versions.V1);
    }

    // v2 is for transit engine in this case MEK is null. MEK is managed by vault internally
    @PostMapping("v2/cipher-store/store")
    public void storeKeysViaTransit(@RequestBody Secret secret) throws Exception {
        secret.setMek(StringUtils.EMPTY);
        keyService.storeKeys(secret, Versions.V2);
    }

    @GetMapping("v2/cipher-store/fetch")
    public Secret fetchKeysViaTransit() throws Exception {
        return keyService.fetchKeys(Versions.V2);
    }


    @PostMapping("v2/save-card")
    public void saveCardViaTransit(@Valid @RequestBody CardStore cardStore) throws Exception {
        cardStoreService.storeCard(cardStore, Versions.V2);
    }

    @GetMapping("v2/fetch-card")
    public CardStore fetchCardViaTransit() throws Exception {
        return cardStoreService.fetchCard(Versions.V2);
    }
}
