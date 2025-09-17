package com.eventra.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID userId;
    private UUID eventId;
    private String status;
    private Double totalPrice;
    private String createdBy;
    private String updatedBy;
}
