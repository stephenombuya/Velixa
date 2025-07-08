package com.velixa.order.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velixa.order.dto.OrderDTO;
import com.velixa.order.model.Order;
import com.velixa.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	@Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setProductIds(orderDTO.getProductIds());
        order.setTotalAmount(orderDTO.getTotalPrice());

        order = orderRepository.save(order);
        orderDTO.setId(order.getId());
        return orderDTO;
    }

    @Override
    public Optional<OrderDTO> getOrderById(String id) {
        return orderRepository.findById(id)
                .map(order -> new OrderDTO(
                        order.getId(),
                        order.getUserId(),
                        order.getProductIds(),
                        order.getTotalAmount()
                ));
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderDTO(
                        order.getId(),
                        order.getUserId(),
                        order.getProductIds(),
                        order.getTotalAmount()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDTO> updateOrder(String id, OrderDTO orderDTO) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.setUserId(orderDTO.getUserId());
                    existing.setProductIds(orderDTO.getProductIds());
                    existing.setTotalAmount(orderDTO.getTotalPrice());
                    
                    Order updated = orderRepository.save(existing);
                    return new OrderDTO(updated.getId(), updated.getUserId(), updated.getProductIds(), updated.getTotalAmount());
                });
    }

    @Override
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }
}
