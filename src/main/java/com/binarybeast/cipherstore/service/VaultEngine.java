package com.binarybeast.cipherstore.service;

import com.binarybeast.cipherstore.secret.Secret;

public interface VaultEngine {
    public void storeKey(Secret secret) throws Exception;
    public Secret fetchKey() throws Exception;
}
