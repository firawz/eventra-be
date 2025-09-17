package com.eventra.service.impl;

import com.eventra.dto.OrderRequest;
import com.eventra.dto.OrderResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Event;
import com.eventra.model.Order;
import com.eventra.model.User;
import com.eventra.repository.EventRepository;
import com.eventra.repository.OrderRepository;
import com.eventra.repository.UserRepository;
import com.eventra.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        return convertToDto(order);
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + orderRequest.getUserId()));
        Event event = eventRepository.findById(orderRequest.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + orderRequest.getEventId()));

        Order order = new Order();
        order.setUser(user);
        order.setEvent(event);
        order.setStatus(orderRequest.getStatus());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setCreatedAt(LocalDateTime.now());
        order.setCreatedBy(orderRequest.getCreatedBy());

        Order savedOrder = orderRepository.save(order);
        return convertToDto(savedOrder);
    }

    @Override
    public OrderResponse updateOrder(UUID id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + orderRequest.getUserId()));
        Event event = eventRepository.findById(orderRequest.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + orderRequest.getEventId()));

        order.setUser(user);
        order.setEvent(event);
        order.setStatus(orderRequest.getStatus());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(orderRequest.getUpdatedBy());

        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    @Override
    public void deleteOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        orderRepository.delete(order);
    }

    private OrderResponse convertToDto(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setEventId(order.getEvent().getId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setCreatedBy(order.getCreatedBy());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setUpdatedBy(order.getUpdatedBy());
        return dto;
    }
}
