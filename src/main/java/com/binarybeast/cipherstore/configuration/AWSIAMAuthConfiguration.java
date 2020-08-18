package com.binarybeast.cipherstore.configuration;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.vault.authentication.AwsIamAuthentication;
import org.springframework.vault.authentication.AwsIamAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.BasicAWSCredentials;
import com.binarybeast.cipherstore.interceptor.RequestLoggingInterceptor;

@Configuration
public class AWSIAMAuthConfiguration extends AbstractVaultConfiguration {

    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

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

    private String access_key = "--your access key--";
    private String secret_key = "-- your secret key -- ";

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

        AwsIamAuthenticationOptions options = AwsIamAuthenticationOptions.builder()
                                                                         .credentials(new BasicAWSCredentials(access_key, secret_key))
                                                                         .role("my-role")
                                                                         .build();
        return new AwsIamAuthentication(options, getRestTemplate());
    }

    @Bean("vaultTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VaultTemplate getVaultTemplate() {
        return new VaultTemplate(vaultEndpoint(), clientAuthentication());
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme(vaultScheme);
        endpoint.setHost(vaultHost);
        endpoint.setPath(vaultPath);
        endpoint.setPort(vaultPort);
        return endpoint;
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        AwsIamAuthenticationOptions options = AwsIamAuthenticationOptions.builder()
                                                                         .credentials(new BasicAWSCredentials(access_key, secret_key))
                                                                         .build();
        return new AwsIamAuthentication(options, new RestTemplate());
    }

    private RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(requestLoggingInterceptor));
        return restTemplate;
    }
}
