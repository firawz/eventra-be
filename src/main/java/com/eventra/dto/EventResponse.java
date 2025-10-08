package com.eventra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private String imageUrl;
    private Integer capacity;
    private String category;
    private String status;
}
