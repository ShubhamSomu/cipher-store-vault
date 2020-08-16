package com.binarybeast.cipherstore.configuration;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthenticationOptions;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

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
       // return new TokenAuthentication(authToken);
        //System.out.println(getVaultEndpoint().getPath());
        CubbyholeAuthenticationOptions options = CubbyholeAuthenticationOptions
            .builder()
            .initialToken(VaultToken.of(authToken))
            .path(getVaultEndpoint().createUriString("cubbyhole/token"))
            .selfLookup(false)
            .build();

        return new CubbyholeAuthentication(options, new RestTemplate());
    }

    public void getRestOperations(){
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.
    }

    @Bean("vaultTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VaultTemplate getVaultTemplate(){
        return new VaultTemplate(getVaultEndpoint(),getTokenAuthentication());
    }
}
