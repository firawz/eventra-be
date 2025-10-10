package com.eventra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

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

    @NotNull(message = "Birth Date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate BirthDate;

    private String createdBy;
    private String updatedBy;
}
