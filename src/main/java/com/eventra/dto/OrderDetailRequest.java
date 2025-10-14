package com.eventra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import jakarta.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequest {

    private UUID orderId; // Will be set by the service

    @NotBlank(message = "NIK cannot be blank")
    private String Nik;

    @NotBlank(message = "Full Name cannot be blank")
    private String FullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String Email;

    @NotBlank(message = "Ticket Code cannot be blank")
    private String TicketCode;

    @NotNull(message = "Ticket ID cannot be null")
    private UUID TicketId;

    private String createdBy;
    private String updatedBy;
}
