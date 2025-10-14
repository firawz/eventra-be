package com.eventra.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID userId;
    private UUID eventId;
    private UUID ticketEventId;
    private UUID ticketId;
    private Integer totalPrice;
    private String createdBy;
    private String updatedBy;
    private List<OrderDetailRequest> orderDetails;
}
