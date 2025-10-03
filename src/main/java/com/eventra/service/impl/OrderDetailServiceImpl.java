package com.eventra.service.impl;

import com.eventra.dto.OrderDetailRequest;
import com.eventra.dto.OrderDetailResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.dto.PaginationResponse;
import com.eventra.model.Order;
import com.eventra.model.OrderDetail;
import com.eventra.repository.OrderDetailRepository;
import com.eventra.repository.OrderRepository;
import com.eventra.service.OrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.eventra.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private static final Logger logger = LoggerFactory.getLogger(OrderDetailServiceImpl.class);

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final AuditService auditService;

    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailRequest orderDetailRequest) {
        try {
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
            auditService.publishAudit(savedOrderDetail, "CREATE");
            return mapToOrderDetailResponse(savedOrderDetail);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during createOrderDetail: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating order detail: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating order detail: " + e.getMessage());
        }
    }

    @Override
    public OrderDetailResponse getOrderDetailById(UUID id) {
        try {
            OrderDetail orderDetail = orderDetailRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("OrderDetail not found with id " + id));
            return mapToOrderDetailResponse(orderDetail);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during getOrderDetailById for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving order detail by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving order detail by ID: " + e.getMessage());
        }
    }

    @Override
    public PaginationResponse<OrderDetailResponse> getAllOrderDetails(int page, int limit) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
            Page<OrderDetail> orderDetailPage = orderDetailRepository.findAll(pageable);

            List<OrderDetailResponse> content = orderDetailPage.getContent().stream()
                    .map(this::mapToOrderDetailResponse)
                    .collect(Collectors.toList());

            return new PaginationResponse<>(
                    content,
                    orderDetailPage.getNumber() + 1,
                    orderDetailPage.getSize(),
                    orderDetailPage.getTotalElements(),
                    orderDetailPage.getTotalPages(),
                    orderDetailPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error retrieving all order details: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving all order details: " + e.getMessage());
        }
    }

    @Override
    public OrderDetailResponse updateOrderDetail(UUID id, OrderDetailRequest orderDetailRequest) {
        try {
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
            auditService.publishAudit(updatedOrderDetail, "UPDATE");
            return mapToOrderDetailResponse(updatedOrderDetail);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during updateOrderDetail for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating order detail with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating order detail: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrderDetail(UUID id) {
        try {
            OrderDetail orderDetail = orderDetailRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("OrderDetail not found with id " + id));
            orderDetailRepository.delete(orderDetail);
            auditService.publishAudit(orderDetail, "DELETE");
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during deleteOrderDetail for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting order detail with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting order detail: " + e.getMessage());
        }
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
