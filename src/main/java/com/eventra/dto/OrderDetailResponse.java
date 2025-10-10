package com.eventra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    private UUID Id;
    private UUID OrderId;
    private String Nik;
    private String FullName;
    private LocalDate BirthDate;
    private String TicketCode;
    private LocalDateTime CreatedAt;
    private String CreatedBy;
    private LocalDateTime UpdatedAt;
    private String UpdatedBy;
}
