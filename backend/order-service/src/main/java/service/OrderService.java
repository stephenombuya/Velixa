package com.velixa.order.service;

import com.velixa.order.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    Optional<OrderDTO> getOrderById(String id);
    List<OrderDTO> getAllOrders();
    Optional<OrderDTO> updateOrder(String id, OrderDTO orderDTO);
    void deleteOrder(String id);
}
