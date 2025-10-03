package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.OrderDetailRequest;
import com.eventra.dto.OrderDetailResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.service.OrderDetailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/order-details")
@AllArgsConstructor
public class OrderDetailController {

    private static final Logger logger = LoggerFactory.getLogger(OrderDetailController.class);

    private final OrderDetailService orderDetailService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrderDetail(@Valid @RequestBody OrderDetailRequest orderDetailRequest) {
        try {
            OrderDetailResponse createdOrderDetail = orderDetailService.createOrderDetail(orderDetailRequest);
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail created successfully", createdOrderDetail), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating order detail: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error creating order detail: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetailById(@PathVariable UUID id) {
        try {
            OrderDetailResponse orderDetailResponse = orderDetailService.getOrderDetailById(id);
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail fetched successfully", orderDetailResponse), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching order detail by ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error fetching order detail: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<OrderDetailResponse>>> getAllOrderDetails(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            PaginationResponse<OrderDetailResponse> orderDetails = orderDetailService.getAllOrderDetails(page, limit);
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Details fetched successfully", orderDetails), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all order details: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error fetching all order details: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrderDetail(@PathVariable UUID id, @Valid @RequestBody OrderDetailRequest orderDetailRequest) {
        try {
            OrderDetailResponse updatedOrderDetail = orderDetailService.updateOrderDetail(id, orderDetailRequest);
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail updated successfully", updatedOrderDetail), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating order detail with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error updating order detail: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrderDetail(@PathVariable UUID id) {
        try {
            orderDetailService.deleteOrderDetail(id);
            return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail deleted successfully", null), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting order detail with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error deleting order detail: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
