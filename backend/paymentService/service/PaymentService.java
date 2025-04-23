package com.velixa.paymentservice.service;

import com.velixa.paymentservice.model.Payment;
import com.velixa.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        // In a real scenario, this would interact with a payment gateway
        payment.setStatus("PENDING");
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Simulate payment processing
        // In a real application, this would be handled asynchronously
        return simulatePaymentProcessing(savedPayment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public Payment updatePaymentStatus(String id, String status) {
        Payment payment = getPaymentById(id);
        
        // Validate status
        if (!isValidStatus(status)) {
            throw new RuntimeException("Invalid payment status: " + status);
        }
        
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }

    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || 
               status.equals("COMPLETED") || 
               status.equals("FAILED");
    }

    private Payment simulatePaymentProcessing(Payment payment) {
        // For demonstration, we're simulating a successful payment
        // In a real application, this would be handled by a payment gateway callback
        payment.setStatus("COMPLETED");
        payment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
}
