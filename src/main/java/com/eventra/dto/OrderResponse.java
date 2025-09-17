package com.eventra.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private UUID eventId;
    private String status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
