package com.velixa.orderservice.service;

import com.velixa.orderservice.model.Order;
import com.velixa.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order updateOrderStatus(String id, String status) {
        Order order = getOrderById(id);
        
        // Validate status
        if (!isValidStatus(status)) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    public Order updateOrderPayment(String id, String paymentId) {
        Order order = getOrderById(id);
        
        order.setPaymentId(paymentId);
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    public void deleteOrder(String id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || 
               status.equals("PAID") || 
               status.equals("SHIPPED") || 
               status.equals("DELIVERED") || 
               status.equals("CANCELLED");
    }
}
