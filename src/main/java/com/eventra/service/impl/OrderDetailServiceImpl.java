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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eventra.config.CustomUserDetails;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime; // Keep LocalDateTime for CreatedAt/UpdatedAt
import com.eventra.model.Event;
import com.eventra.model.EventStatus;
import com.eventra.model.User;

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

            // Retrieve Event and User from the Order
            Event event = order.getEvent();
            User user = order.getUser();

            // Check event status
            if (event.getStatus().toString().equals(EventStatus.CANCELED.toString()) || event.getStatus().toString().equals(EventStatus.FINISHED.toString())) {
                throw new RuntimeException("Cannot create order detail: Event is " + event.getStatus().toString());
            }

            // Generate ticketCode
            String eventTitlePrefix = event.getTitle().substring(0, Math.min(event.getTitle().length(), 2)).toUpperCase();
            String randomNumber = generateRandomNumberString(6);
            String userEmailPrefix = user.getEmail().substring(0, Math.min(user.getEmail().length(), 2)).toUpperCase();

            String ticketCode = eventTitlePrefix + randomNumber + userEmailPrefix;

            // BirthDate has been replaced with Email.
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .Nik(orderDetailRequest.getNik())
                    .FullName(orderDetailRequest.getFullName().toUpperCase())
                    .Email(orderDetailRequest.getEmail())
                    .TicketCode(ticketCode) // Set the generated ticketCode
                    .TicketId(orderDetailRequest.getTicketId()) // Set ticketId from request
                    .CreatedBy(getCurrentAuditor()) // Set createdBy from security context
                    .build();

            OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
            auditService.publishCreateAudit(savedOrderDetail, "CREATE"); // Use new audit method
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
            existingOrderDetail.setEmail(orderDetailRequest.getEmail());
            existingOrderDetail.setTicketId(orderDetailRequest.getTicketId()); // Set ticketId from request
            existingOrderDetail.setUpdatedBy(getCurrentAuditor()); // Set updatedBy from security context

            OrderDetail updatedOrderDetail = orderDetailRepository.save(existingOrderDetail);
            auditService.publishUpdateAudit(updatedOrderDetail, "UPDATE"); // Use new audit method
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
            auditService.publishDeleteAudit(orderDetail, "DELETE"); // Use new audit method
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during deleteOrderDetail for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting order detail with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting order detail: " + e.getMessage());
        }
    }

    private String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) principal;
                return customUserDetails.getUserId().toString();
            } else {
                logger.warn("Principal is not CustomUserDetails. Returning 'SYSTEM' for auditor.");
                return "SYSTEM";
            }
        }
        return "SYSTEM";
    }

    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .Id(orderDetail.getId())
                .OrderId(orderDetail.getOrder().getId())
                .Nik(orderDetail.getNik())
                .FullName(orderDetail.getFullName())
                .Email(orderDetail.getEmail())
                .TicketCode(orderDetail.getTicketCode())
                .TicketId(orderDetail.getTicketId())
                .CreatedAt(orderDetail.getCreatedAt())
                .CreatedBy(orderDetail.getCreatedBy())
                .UpdatedAt(orderDetail.getUpdatedAt())
                .UpdatedBy(orderDetail.getUpdatedBy())
                .build();
    }

    private String generateRandomNumberString(int length) {
        Random random = new Random();
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int randomNumber = random.nextInt(max - min + 1) + min;
        return String.format("%0" + length + "d", randomNumber);
    }
}
