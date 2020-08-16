package com.binarybeast.cipherstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.binarybeast.cipherstore.repository.SecretRepository;
import com.binarybeast.cipherstore.dao.Secret;

@Service
public class SecretService {

    private final SecretRepository secretRepository;

    public SecretService(SecretRepository secretRepository) {
        this.secretRepository = Objects.requireNonNull(secretRepository, "secretRepository");
    }

    public void saveOrUpdate(Secret secret) {
        secretRepository.save(secret);
    }

    public Secret fetch(){
        List<Secret> secrets = new ArrayList<>();
        secretRepository.findAll().forEach(secrets::add);
        return secrets.get(0);
    }

    public void deleteAll(){
        secretRepository.deleteAll();
    }
}
