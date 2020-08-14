package com.binarybeast.cipherstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.binarybeast.cipherstore.secret.Secret;

@Repository
public interface SecretRepository extends CrudRepository<Secret, Integer> {
}