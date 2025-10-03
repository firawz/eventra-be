package com.eventra.service.impl;

import com.eventra.dto.OrderRequest;
import com.eventra.dto.OrderResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Event;
import com.eventra.dto.PaginationResponse;
import com.eventra.model.Order;
import com.eventra.model.User;
import com.eventra.repository.EventRepository;
import com.eventra.repository.OrderRepository;
import com.eventra.repository.UserRepository;
import com.eventra.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.eventra.service.AuditService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuditService auditService;

    @Override
    public PaginationResponse<OrderResponse> getAllOrders(int page, int limit) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
            Page<Order> orderPage = orderRepository.findAll(pageable);

            List<OrderResponse> content = orderPage.getContent().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new PaginationResponse<>(
                    content,
                    orderPage.getNumber() + 1,
                    orderPage.getSize(),
                    orderPage.getTotalElements(),
                    orderPage.getTotalPages(),
                    orderPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error retrieving all orders: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving all orders: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
            return convertToDto(order);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during getOrderById for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving order by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving order by ID: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        try {
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
            auditService.publishAudit(savedOrder, "CREATE");
            return convertToDto(savedOrder);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during createOrder: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }

    @Override
    public OrderResponse updateOrder(UUID id, OrderRequest orderRequest) {
        try {
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
            auditService.publishAudit(updatedOrder, "UPDATE");
            return convertToDto(updatedOrder);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during updateOrder for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating order with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating order: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrder(UUID id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
            orderRepository.delete(order);
            auditService.publishAudit(order, "DELETE");
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during deleteOrder for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting order with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting order: " + e.getMessage());
        }
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
