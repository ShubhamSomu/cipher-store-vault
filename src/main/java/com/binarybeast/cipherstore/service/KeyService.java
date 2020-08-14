package com.binarybeast.cipherstore.service;

import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.binarybeast.cipherstore.secret.Secret;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KeyService {
    //private static final Logger log = LoggerFactory.getLogger(KeyStoreService.class);
    private final ApplicationContext applicationContext;

    public KeyService(ApplicationContext applicationContext) {
        this.applicationContext = Objects.requireNonNull(applicationContext, "applicationContext");
    }

    public void storeKeys(Secret secret, String version) throws Exception {
        VaultEngine vaultEngine;
        if (ObjectUtils.isEmpty(secret) || StringUtils.isEmpty(version)) { throw new IllegalArgumentException("Illegal parameters - secret or version can't be null"); }

        vaultEngine = this.getVaultEngine(version);

        log.debug("Version: /{} ,\n Using CLASS : {} \n", version, vaultEngine.getClass().getSimpleName());
        log.trace("MEK will be null if using Transit Engine");
        log.trace("Plain DEK : {} ,\n Plain MEK : {} \n", secret.getDek(), secret.getMek());

        vaultEngine.storeKey(secret);
    }

    public Secret fetchKeys(String version) throws Exception {
        VaultEngine vaultEngine;

        if (StringUtils.isEmpty(version)) { throw new IllegalArgumentException("Illegal parameters - secret or version can't be null"); }

        vaultEngine = this.getVaultEngine(version);

        log.debug("Version: /{} ,\n Using CLASS : {} \n", version, vaultEngine.getClass().getSimpleName());
        log.trace("MEK will be null if using Transit Engine");

        Secret secret = vaultEngine.fetchKey();
        log.trace("Plain DEK : {} ,\n Plain MEK : {} \n", secret.getDek(), secret.getMek());
        return secret;
    }

    public VaultEngine getVaultEngine(String version) {
        VaultEngine vaultEngine;
        switch (version) {
            case "v1":
                vaultEngine = applicationContext.getBean(KvEngineService.class);
                break;
            case "v2":
                vaultEngine = applicationContext.getBean(TransitEngineService.class);
                break;
            default:
                throw new IllegalArgumentException("Illegal Version");

        }
        return vaultEngine;
    }
}
