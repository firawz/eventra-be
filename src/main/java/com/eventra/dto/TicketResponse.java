package com.eventra.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketResponse {
    private UUID id;
    private EventResponse event;
    private String ticketCategory;
    private Integer price;
    private Integer quota;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
