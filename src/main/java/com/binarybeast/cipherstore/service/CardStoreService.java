package com.binarybeast.cipherstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.binarybeast.cipherstore.dao.CardStore;
import com.binarybeast.cipherstore.dao.Secret;
import com.binarybeast.cipherstore.repository.CardStoreRepository;
import com.binarybeast.cipherstore.types.Versions;
import com.binarybeast.cipherstore.utility.GeneralUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CardStoreService {
    private final ApplicationContext applicationContext;
    private final CryptoService cryptoService;
    private final CardStoreRepository cardStoreRepository;

    public CardStoreService(ApplicationContext applicationContext, CryptoService cryptoService, CardStoreRepository cardStoreRepository) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext");
        this.cryptoService = Objects.requireNonNull(cryptoService, "cryptoService");
        this.cardStoreRepository = Objects.requireNonNull(cardStoreRepository, "cardStoreRepository");
    }

    public void storeCard(CardStore cardStore, Versions version) throws Exception {
        cardStoreRepository.deleteAll();

        VaultEngine vaultEngine = GeneralUtils.getVaultEngine(version, applicationContext);
        Secret secret = vaultEngine.fetchKey();

        // DEK should not be null, as card encrypt is concerned with DEK only and not MEK
        if (ObjectUtils.isEmpty(secret) || StringUtils.isBlank(secret.getDek())) { throw new RuntimeException("Got null from secret Engine"); }

        String plainCardNum = cardStore.getCardNumber();
        String cipherCardNum = cryptoService.encrypt(plainCardNum, secret.getDek());
        cardStore.setCardNumber(cipherCardNum);

        cardStoreRepository.save(cardStore);
    }

    public CardStore fetchCard(Versions version) throws Exception {
        List<CardStore> cardStores = new ArrayList<>();
        cardStoreRepository.findAll().forEach(cardStores::add);

        if (cardStores.isEmpty()) {
            log.error("No cards at DB");
            return null;
        }

        CardStore cardStore = cardStores.get(0);

        VaultEngine vaultEngine = GeneralUtils.getVaultEngine(version, applicationContext);
        Secret secret = vaultEngine.fetchKey();

        // atlease DEK should not be null
        if (ObjectUtils.isEmpty(secret) || StringUtils.isBlank(secret.getDek())) { throw new RuntimeException("Got null from secret Engine"); }

        String cipherCardNum = cardStore.getCardNumber();
        String plainCardNum = cryptoService.decrypt(cipherCardNum, secret.getDek());
        cardStore.setCardNumber(plainCardNum);

        return cardStore;
    }
}
