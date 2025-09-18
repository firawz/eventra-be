package com.eventra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequest {

    @NotNull(message = "Order ID cannot be null")
    private UUID OrderId;

    @NotBlank(message = "NIK cannot be blank")
    private String Nik;

    @NotBlank(message = "Full Name cannot be blank")
    private String FullName;

    @NotNull(message = "Birth Date cannot be null")
    private LocalDateTime BirthDate;

    @NotBlank(message = "Ticket Code cannot be blank")
    private String TicketCode;

    private String CreatedBy;
    private String UpdatedBy;
}
