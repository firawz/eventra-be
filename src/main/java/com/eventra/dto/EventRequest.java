package com.eventra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    @NotBlank(message = "Location cannot be empty")
    private String location;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;

    private String createdBy;
    private String updatedBy;
    private String imageUrl;
}
