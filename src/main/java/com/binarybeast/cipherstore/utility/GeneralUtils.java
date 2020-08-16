package com.binarybeast.cipherstore.utility;

import org.springframework.context.ApplicationContext;

import com.binarybeast.cipherstore.service.KvEngineService;
import com.binarybeast.cipherstore.service.TransitEngineService;
import com.binarybeast.cipherstore.service.VaultEngine;
import com.binarybeast.cipherstore.types.Versions;

public class GeneralUtils {
    public static VaultEngine getVaultEngine(Versions version, ApplicationContext applicationContext) {
        VaultEngine vaultEngine;
        switch (version) {
            case V1:
                vaultEngine = applicationContext.getBean(KvEngineService.class);
                break;
            case V2:
                vaultEngine = applicationContext.getBean(TransitEngineService.class);
                break;
            default:
                throw new IllegalArgumentException("Illegal Version");

        }
        return vaultEngine;
    }
}
