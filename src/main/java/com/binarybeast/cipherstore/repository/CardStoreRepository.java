package com.binarybeast.cipherstore.repository;

import org.springframework.data.repository.CrudRepository;

import com.binarybeast.cipherstore.dao.CardStore;

public interface CardStoreRepository extends CrudRepository<CardStore, Integer> {
}
