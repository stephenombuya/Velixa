package com.velixa.order.repository;

import com.velixa.order.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
    // You can add custom queries here later if needed
}
