package com.eventra.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TicketRequest {
    @NotNull(message = "Event ID cannot be null")
    private UUID eventId;

    @NotBlank(message = "Ticket category cannot be blank")
    private String ticketCategory;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Quota cannot be null")
    @Positive(message = "Quota must be positive")
    private Integer quota;
}
