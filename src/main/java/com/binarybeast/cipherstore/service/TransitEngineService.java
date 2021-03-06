package com.binarybeast.cipherstore.service;

/*

  in case of transit Key Engine, we won't need MEK, as MEK is internally managed by vault,
  We will only supply DEK to vault, vault will give cipher_DEK back
  Save this cipher DEK to DB.
  Now, when request comes to save CARD DETAILS,
  we will need plain DEK to encrypt Card Details to save in DB

  -- soln --
  In this case,
  First, query our DB to get Cipher DEK,
  request vault to decyrpt cipher DEK, it will give back plain DEK,
  Now, Use this to plain DEK to encrypt Card Details

  save encrypt card details to DB
 */

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.core.VaultWrappingOperations;
import org.springframework.vault.support.VaultMount;

import com.binarybeast.cipherstore.dao.Secret;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransitEngineService implements VaultEngine, InitializingBean {
    private final VaultTemplate vaultTemplate;
    private final SecretService secretService;

    @Value("${cipher.vault.namespace-transit}")
    private String secretNamespace;

    static {
        log.debug("Using Transit Engine");
    }

    public TransitEngineService(@Qualifier("vaultTemplate") VaultTemplate vaultTemplate,
                                SecretService secretService) {
        this.vaultTemplate = Objects.requireNonNull(vaultTemplate,"vaultTemplate");
        this.secretService = Objects.requireNonNull(secretService, "secretService");
    }


    private boolean isTransitExists() {
        VaultSysOperations vaultSysOperations = vaultTemplate.opsForSys();
        return vaultSysOperations.getMounts().containsKey("transit/");
    }

    private boolean isNameSpaceExists() {
        VaultTransitOperations vaultTransitOperations = vaultTemplate.opsForTransit();
        return vaultTransitOperations.getKeys().contains(secretNamespace);
    }

    private void checkAndCreateMount() {
        if (!isTransitExists()) {
            VaultSysOperations vaultSysOperations = vaultTemplate.opsForSys();
            // mounting(enabling) transit engine, accessed via path "transit/"
            vaultSysOperations.mount("transit", VaultMount.create("transit"));
        }
    }

    private void checkAndCreateNameSpace() {
        if (!isNameSpaceExists()) {
            VaultTransitOperations vaultTransitOperations = vaultTemplate.opsForTransit();
            // create our namespace, from where cipher , uncipher will occur
            vaultTransitOperations.createKey(secretNamespace);
        }
    }

    private void deleteExisting(){
        if(isTransitExists() || isNameSpaceExists()){
            VaultTransitOperations vaultTransitOperations = vaultTemplate.opsForTransit();
            vaultTransitOperations.deleteKey(secretNamespace);
        }
    }

    @Override
    public void storeKey(Secret secret) throws Exception {
        //todo shubham keep only 1
        // deleteExisting();
        /**
         * to delete a namespace need to set deletion to true :-
         *  > vault write transit/keys/cipherDEKGen/config deletion_allowed=true
         */

        this.checkAndCreateMount();
        this.checkAndCreateNameSpace();

        //todo shubham keep only 1
        secretService.deleteAll(); // Its bad, but needed, i don't want to add many keys for testing

        VaultTransitOperations vaultTransitOperations = vaultTemplate.opsForTransit();
        String cipherDek = vaultTransitOperations.encrypt(secretNamespace, secret.getDek());

        secret.setDek(cipherDek);

        log.trace("----- Saving Cipher DEK to DB: {} ------", cipherDek);

        secretService.saveOrUpdate(secret);
    }

    @Override
    public Secret fetchKey() throws Exception {
        Secret secret = secretService.fetch();

        if (Objects.isNull(secret)) { return null; }

        log.trace("----- Fetching Cipher DEK from DB: {} ------", secret.getDek());

        VaultTransitOperations vaultTransitOperations = vaultTemplate.opsForTransit();
        String plainDek = vaultTransitOperations.decrypt(secretNamespace, secret.getDek());

        log.trace("----- Fetching Plain DEK from Vault: {} ------", plainDek);

        //todo shubham check this in mybatis
         /* need to do this... as if i do secret.setDek(plainDek)
            then hibernate will update this row in DB because it is in Persistent state
         */
        Secret newSecret = new Secret();
        newSecret.setDek(plainDek);
        return newSecret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(secretNamespace) || StringUtils.equals(secretNamespace, "${cipher.vault.namespace-transit}")) {
            throw new IllegalArgumentException("NameSpace for transit engine must be configured in properties");
        }
    }
}