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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<EventResponse>>> getAllEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String sortByDate) {
        PaginationResponse<EventResponse> events = eventService.getAllEvents(page, limit, title, description, sortByDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Events retrieved successfully", events));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id)
                .map(eventResponse -> ResponseEntity.ok(new ApiResponse<>(true, "Event retrieved successfully", eventResponse)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Event not found", null)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        EventResponse createdEvent = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Event created successfully", createdEvent));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(@PathVariable UUID id, @Valid @RequestBody EventRequest eventRequest) {
        return eventService.updateEvent(id, eventRequest)
                .map(eventResponse -> ResponseEntity.ok(new ApiResponse<>(true, "Event updated successfully", eventResponse)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Event not found", null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable UUID id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Event deleted successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Event not found", null));
    }

    // Generic API Response class
    
}
