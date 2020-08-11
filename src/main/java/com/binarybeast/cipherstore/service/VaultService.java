package com.binarybeast.cipherstore.service;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import com.binarybeast.cipherstore.secret.Secret;

@Service
public class VaultService {
    private final VaultEndpoint vaultEndpoint;

    private final TokenAuthentication tokenAuthentication;

    @Value("${cipher.vault.namespace}")
    private String secretNamespace;

    public VaultService(@Qualifier("vaultEndpoint") VaultEndpoint vaultEndpoint,
                        @Qualifier("tokenAuthentication") TokenAuthentication tokenAuthentication) {
        this.vaultEndpoint = Objects.requireNonNull(vaultEndpoint, "vaultEndpoint");
        this.tokenAuthentication = Objects.requireNonNull(tokenAuthentication, "tokenAuthentication");
    }

    public VaultTemplate getVaultTemplate() {
       return new VaultTemplate(vaultEndpoint, tokenAuthentication);
    }

    public void storeInVault(Secret secret) throws Exception {
        if(StringUtils.isBlank(secretNamespace)){
            throw new Exception("secretName space is null in properties");
        }

        getVaultTemplate().write(secretNamespace, secret);
    }

    public Secret fetchFromVault() throws Exception {
        if(StringUtils.isBlank(secretNamespace)){
            throw new Exception("secretName space is null in properties");
        }
        VaultResponseSupport<Secret> support = getVaultTemplate().read(secretNamespace, Secret.class);
        return support.getData();
    }
}
