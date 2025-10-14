package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.EventRequest;
import com.eventra.dto.EventResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<EventResponse>>> getAllEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortByDate) {
        try {
            PaginationResponse<EventResponse> events = eventService.getAllEvents(page, limit, title, description, category, status, sortByDate);
            return ResponseEntity.ok(new ApiResponse<>(true, "Events retrieved successfully", events));
        } catch (Exception e) {
            logger.error("Error retrieving all events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error retrieving events: " + e.getMessage(), null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable UUID id) {
        try {
            return eventService.getEventById(id)
                    .map(eventResponse -> ResponseEntity.ok(new ApiResponse<>(true, "Event retrieved successfully", eventResponse)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Event not found", null)));
        } catch (Exception e) {
            logger.error("Error retrieving event by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error retrieving event: " + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        try {
            // --- DEBUGGING LOG ---
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("User '{}' attempting to create event with authorities: {}", authentication.getName(), authentication.getAuthorities());
            // ---------------------

            EventResponse createdEvent = eventService.createEvent(eventRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Event created successfully", createdEvent));
        } catch (Exception e) {
            logger.error("Error creating event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error creating event: " + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(@PathVariable UUID id, @RequestBody EventRequest eventRequest) {
        try {
            return eventService.updateEvent(id, eventRequest)
                    .map(eventResponse -> ResponseEntity.ok(new ApiResponse<>(true, "Event updated successfully", eventResponse)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Event not found", null)));
        } catch (Exception e) {
            logger.error("Error updating event with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error updating event: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable UUID id) {
        try {
            if (eventService.deleteEvent(id)) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Event deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Event not found", null));
        } catch (Exception e) {
            logger.error("Error deleting event with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error deleting event: " + e.getMessage(), null));
        }
    }

    
}
