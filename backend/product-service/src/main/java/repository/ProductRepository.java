package com.velixa.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.velixa.product.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {}
