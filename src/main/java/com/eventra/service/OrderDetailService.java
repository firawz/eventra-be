package com.eventra.service;

import com.eventra.dto.OrderDetailRequest;
import com.eventra.dto.OrderDetailResponse;
import com.eventra.dto.PaginationResponse;

import java.util.List;
import java.util.UUID;

public interface OrderDetailService {
    OrderDetailResponse createOrderDetail(OrderDetailRequest orderDetailRequest);
    OrderDetailResponse getOrderDetailById(UUID id);
    PaginationResponse<OrderDetailResponse> getAllOrderDetails(int page, int limit);
    OrderDetailResponse updateOrderDetail(UUID id, OrderDetailRequest orderDetailRequest);
    void deleteOrderDetail(UUID id);
}
