package com.eventra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotBlank(message = "Location cannot be empty")
    private String location;

    @NotNull(message = "Start date cannot be null")
    private OffsetDateTime startDate;

    @NotNull(message = "End date cannot be null")
    private OffsetDateTime endDate;

    private String createdBy;
    private String updatedBy;
    private String imageUrl;

    @NotBlank(message = "Category cannot be empty")
    private String category;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    private List<TicketRequest> tickets;
}
