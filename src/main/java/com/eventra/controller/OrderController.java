package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.OrderRequest;
import com.eventra.dto.OrderResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            PaginationResponse<OrderResponse> orders = orderService.getAllOrders(page, limit);
            return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
        } catch (Exception e) {
            logger.error("Error retrieving all orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error retrieving orders: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", order));
        } catch (Exception e) {
            logger.error("Error retrieving order by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error retrieving order: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse createdOrder = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(new ApiResponse<>(true, "Order created successfully", createdOrder), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error creating order: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(@PathVariable UUID id, @RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse updatedOrder = orderService.updateOrder(id, orderRequest);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order updated successfully", updatedOrder));
        } catch (Exception e) {
            logger.error("Error updating order with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error updating order: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Order deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting order with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error deleting order: " + e.getMessage(), null));
        }
    }
}
