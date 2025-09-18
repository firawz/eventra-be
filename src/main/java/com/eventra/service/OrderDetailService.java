package com.eventra.service;

import com.eventra.dto.OrderDetailRequest;
import com.eventra.dto.OrderDetailResponse;

import java.util.List;
import java.util.UUID;

public interface OrderDetailService {
    OrderDetailResponse createOrderDetail(OrderDetailRequest orderDetailRequest);
    OrderDetailResponse getOrderDetailById(UUID id);
    List<OrderDetailResponse> getAllOrderDetails();
    OrderDetailResponse updateOrderDetail(UUID id, OrderDetailRequest orderDetailRequest);
    void deleteOrderDetail(UUID id);
}
