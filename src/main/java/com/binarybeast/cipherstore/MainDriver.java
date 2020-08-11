package com.binarybeast.cipherstore;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import com.binarybeast.cipherstore.secret.Secret;
/* No use of this class */
public class MainDriver {
    private static String SECRET_NAMESPACE = "secret/cipherStore";
    public static void main(String[] args) {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme("http");

        VaultTemplate vaultTemplate = new VaultTemplate(endpoint,new TokenAuthentication("00000000-0000-0000-0000-000000000000"));

        Secret secret = new Secret();
        secret.setMek("pass");
        secret.setDek("soma");

        vaultTemplate.write(SECRET_NAMESPACE, secret);
/*        VaultResponseSupport<Secret> responseSupport = vaultTemplate.read(SECRET_NAMESPACE, Secret.class);
        System.out.println(responseSupport.getData().getUsername());
        System.out.println(responseSupport.getData().getPassword());*/

        VaultResponseSupport support = vaultTemplate.read(SECRET_NAMESPACE);
        System.out.println(support);
        System.out.println("somthing");

    }
}
