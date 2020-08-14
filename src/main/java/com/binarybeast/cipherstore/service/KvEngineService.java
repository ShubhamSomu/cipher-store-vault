package com.binarybeast.cipherstore.service;

import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import com.binarybeast.cipherstore.secret.Secret;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KvEngineService implements VaultEngine, InitializingBean {
    private final VaultEndpoint vaultEndpoint;
    private final TokenAuthentication tokenAuthentication;
    private final CryptoService cryptoService;

    @Value("${cipher.vault.namespace-kv}")
    private String secretNamespace;

    static {
        log.debug("Using KV Engine");
    }

    public KvEngineService(@Qualifier("vaultEndpoint") VaultEndpoint vaultEndpoint,
                           @Qualifier("tokenAuthentication") TokenAuthentication tokenAuthentication,
                           CryptoService cryptoService) {
        this.vaultEndpoint = Objects.requireNonNull(vaultEndpoint, "vaultEndpoint");
        this.tokenAuthentication = Objects.requireNonNull(tokenAuthentication, "tokenAuthentication");
        this.cryptoService = Objects.requireNonNull(cryptoService, "cryptoService");
    }

    public VaultTemplate getVaultTemplate() {
        return new VaultTemplate(vaultEndpoint, tokenAuthentication);
    }

    @Override
    public void storeKey(Secret secret) throws Exception {
        if (StringUtils.isBlank(secretNamespace)) {
            throw new Exception("secretName space is null in properties");
        }

        if (ObjectUtils.isEmpty(secret) || StringUtils.isBlank(secret.getDek()) || StringUtils.isBlank(secret.getMek())) { throw new IllegalArgumentException("MEK or DEK can't be null"); }

        String cipherDek = cryptoService.encrypt(secret.getDek(), secret.getMek());

        secret.setDek(cipherDek);
        getVaultTemplate().write(secretNamespace, secret);
    }

    /**
     * -- Another way to fetch via KV --
     * VaultResponse response = vaultTemplate.opsForKeyValue("secret", KeyValueBackend.KV_2).get("tb-pci-dss");
     * String kvSecretKey = response.getData().get("data.enc.key").toString();
     * 		System.out.println(kvSecretKey);
     */
    @Override
    public Secret fetchKey() throws Exception {
        if (StringUtils.isBlank(secretNamespace)) {
            throw new Exception("secretName space is null in properties");
        }

        VaultResponseSupport<Secret> support = getVaultTemplate().read(secretNamespace, Secret.class);

        if (ObjectUtils.isEmpty(support)) { return new Secret(); }

        Secret tempSecret = support.getData();

        if (ObjectUtils.isEmpty(tempSecret) || StringUtils.isBlank(tempSecret.getDek()) || StringUtils.isBlank(tempSecret.getMek())) { throw new IllegalArgumentException("Received Null MEK or DEK from Vault"); }

        String plainDek = cryptoService.decrypt(tempSecret.getDek(), tempSecret.getMek());

        tempSecret.setDek(plainDek);
        return tempSecret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(secretNamespace) || StringUtils.equals(secretNamespace, "${cipher.vault.namespace-kv}")) {
            throw new IllegalArgumentException("Namespace for KV engine must be configured in properties");
        }
    }
}