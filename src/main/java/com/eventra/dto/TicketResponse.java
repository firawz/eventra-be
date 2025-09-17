package com.eventra.dto;

import lombok.Data;

import com.eventra.dto.EventResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketResponse {
    private UUID id;
    private EventResponse event;
    private String ticketCategory;
    private BigDecimal price;
    private Integer quota;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
