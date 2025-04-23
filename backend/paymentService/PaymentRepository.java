package com.velixa.paymentservice.repository;

import com.velixa.paymentservice.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByUserId(String userId);
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByStatus(String status);
}
