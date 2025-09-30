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

@RestController
@RequestMapping("/api/order-details")
@AllArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrderDetail(@Valid @RequestBody OrderDetailRequest orderDetailRequest) {
        OrderDetailResponse createdOrderDetail = orderDetailService.createOrderDetail(orderDetailRequest);
        return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail created successfully", createdOrderDetail), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetailById(@PathVariable UUID id) {
        OrderDetailResponse orderDetailResponse = orderDetailService.getOrderDetailById(id);
        return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail fetched successfully", orderDetailResponse), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<OrderDetailResponse>>> getAllOrderDetails(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PaginationResponse<OrderDetailResponse> orderDetails = orderDetailService.getAllOrderDetails(page, limit);
        return new ResponseEntity<>(new ApiResponse<>(true, "Order Details fetched successfully", orderDetails), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrderDetail(@PathVariable UUID id, @Valid @RequestBody OrderDetailRequest orderDetailRequest) {
        OrderDetailResponse updatedOrderDetail = orderDetailService.updateOrderDetail(id, orderDetailRequest);
        return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail updated successfully", updatedOrderDetail), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrderDetail(@PathVariable UUID id) {
        orderDetailService.deleteOrderDetail(id);
        return new ResponseEntity<>(new ApiResponse<>(true, "Order Detail deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}
