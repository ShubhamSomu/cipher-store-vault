package com.binarybeast.cipherstore.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

@Configuration
public class CiperStoreConfiguration {

    @Value("${cipher.vault.scheme}")
    private String vaultScheme;

    @Value("${cipher.vault.path}")
    private String vaultPath;

    @Value("${cipher.vault.port}")
    private int vaultPort;

    @Value("${cipher.vault.host}")
    private String vaultHost;

    @Value("${cipher.vault.auth.token}")
    private String authToken;

    @Bean("vaultEndpoint")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VaultEndpoint getVaultEndpoint() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme(vaultScheme);
        endpoint.setHost(vaultHost);
        endpoint.setPath(vaultPath);
        endpoint.setPort(vaultPort);
        return endpoint;
    }

    @Bean("tokenAuthentication")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ClientAuthentication getTokenAuthentication() {
        return new TokenAuthentication(authToken);
    }



    @Bean("vaultTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VaultTemplate getVaultTemplate(){
        return new VaultTemplate(getVaultEndpoint(),getTokenAuthentication());
    }
}
