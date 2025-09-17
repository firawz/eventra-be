package com.eventra.service;

import com.eventra.dto.OrderRequest;
import com.eventra.dto.OrderResponse;
import com.eventra.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(UUID id);
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse updateOrder(UUID id, OrderRequest orderRequest);
    void deleteOrder(UUID id);
}
