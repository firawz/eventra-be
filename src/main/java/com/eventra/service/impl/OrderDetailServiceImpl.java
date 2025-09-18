package com.eventra.service.impl;

import com.eventra.dto.OrderDetailRequest;
import com.eventra.dto.OrderDetailResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Order;
import com.eventra.model.OrderDetail;
import com.eventra.repository.OrderDetailRepository;
import com.eventra.repository.OrderRepository;
import com.eventra.service.OrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailRequest orderDetailRequest) {
        Order order = orderRepository.findById(orderDetailRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderDetailRequest.getOrderId()));

        OrderDetail orderDetail = OrderDetail.builder()
                .Order(order)
                .Nik(orderDetailRequest.getNik())
                .FullName(orderDetailRequest.getFullName())
                .BirthDate(orderDetailRequest.getBirthDate())
                .TicketCode(orderDetailRequest.getTicketCode())
                .CreatedBy(orderDetailRequest.getCreatedBy())
                .build();

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
        return mapToOrderDetailResponse(savedOrderDetail);
    }

    @Override
    public OrderDetailResponse getOrderDetailById(UUID id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderDetail not found with id " + id));
        return mapToOrderDetailResponse(orderDetail);
    }

    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        return orderDetailRepository.findAll().stream()
                .map(this::mapToOrderDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailResponse updateOrderDetail(UUID id, OrderDetailRequest orderDetailRequest) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderDetail not found with id " + id));

        Order order = orderRepository.findById(orderDetailRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderDetailRequest.getOrderId()));

        existingOrderDetail.setOrder(order);
        existingOrderDetail.setNik(orderDetailRequest.getNik());
        existingOrderDetail.setFullName(orderDetailRequest.getFullName());
        existingOrderDetail.setBirthDate(orderDetailRequest.getBirthDate());
        existingOrderDetail.setTicketCode(orderDetailRequest.getTicketCode());
        existingOrderDetail.setUpdatedBy(orderDetailRequest.getUpdatedBy());

        OrderDetail updatedOrderDetail = orderDetailRepository.save(existingOrderDetail);
        return mapToOrderDetailResponse(updatedOrderDetail);
    }

    @Override
    public void deleteOrderDetail(UUID id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderDetail not found with id " + id));
        orderDetailRepository.delete(orderDetail);
    }

    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .Id(orderDetail.getId())
                .OrderId(orderDetail.getOrder().getId())
                .Nik(orderDetail.getNik())
                .FullName(orderDetail.getFullName())
                .BirthDate(orderDetail.getBirthDate())
                .TicketCode(orderDetail.getTicketCode())
                .CreatedAt(orderDetail.getCreatedAt())
                .CreatedBy(orderDetail.getCreatedBy())
                .UpdatedAt(orderDetail.getUpdatedAt())
                .UpdatedBy(orderDetail.getUpdatedBy())
                .build();
    }
}
