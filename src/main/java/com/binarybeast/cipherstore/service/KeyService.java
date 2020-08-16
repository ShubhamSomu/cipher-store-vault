package com.binarybeast.cipherstore.service;

import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.binarybeast.cipherstore.dao.Secret;
import com.binarybeast.cipherstore.types.Versions;
import com.binarybeast.cipherstore.utility.GeneralUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KeyService {
    //private static final Logger log = LoggerFactory.getLogger(KeyStoreService.class);
    private final ApplicationContext applicationContext;

    public KeyService(ApplicationContext applicationContext) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext");
    }

    public void storeKeys(Secret secret, Versions version) throws Exception {
        VaultEngine vaultEngine;
        if (ObjectUtils.isEmpty(secret)) { throw new IllegalArgumentException("Secret can't be null"); }

        vaultEngine = GeneralUtils.getVaultEngine(version, applicationContext);

        log.debug("Version: /{} ,\n Using CLASS : {} \n", version, vaultEngine.getClass().getSimpleName());
        log.trace("MEK will be null if using Transit Engine");
        log.trace("Plain DEK : {} ,\n Plain MEK : {} \n", secret.getDek(), secret.getMek());

        vaultEngine.storeKey(secret);
    }

    public Secret fetchKeys(Versions version) throws Exception {
        VaultEngine vaultEngine;

        //if (StringUtils.isEmpty(version)) { throw new IllegalArgumentException("Illegal parameters - secret or version can't be null"); }

        vaultEngine = GeneralUtils.getVaultEngine(version, applicationContext);

        log.debug("Version: /{} ,\n Using CLASS : {} \n", version, vaultEngine.getClass().getSimpleName());
        log.trace("MEK will be null if using Transit Engine");

        Secret secret = vaultEngine.fetchKey();
        log.trace("Plain DEK : {} ,\n Plain MEK : {} \n", secret.getDek(), secret.getMek());
        return secret;
    }
}
