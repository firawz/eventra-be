package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.dto.TicketRequest;
import com.eventra.dto.TicketResponse;
import com.eventra.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        try {
            TicketResponse ticketResponse = ticketService.createTicket(ticketRequest);
            return new ResponseEntity<>(new ApiResponse<>(true, "Ticket created successfully", ticketResponse), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating ticket: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error creating ticket: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<TicketResponse>>> getAllTickets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            PaginationResponse<TicketResponse> tickets = ticketService.getAllTickets(page, limit);
            return new ResponseEntity<>(new ApiResponse<>(true, "Tickets retrieved successfully", tickets), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving all tickets: {}", e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error retrieving tickets: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable UUID id) {
        try {
            TicketResponse ticketResponse = ticketService.getTicketById(id);
            return new ResponseEntity<>(new ApiResponse<>(true, "Ticket retrieved successfully", ticketResponse), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving ticket by ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error retrieving ticket: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(@PathVariable UUID id, @Valid @RequestBody TicketRequest ticketRequest) {
        try {
            TicketResponse updatedTicket = ticketService.updateTicket(id, ticketRequest);
            return new ResponseEntity<>(new ApiResponse<>(true, "Ticket updated successfully", updatedTicket), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating ticket with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error updating ticket: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable UUID id) {
        try {
            ticketService.deleteTicket(id);
            return new ResponseEntity<>(new ApiResponse<>(true, "Ticket deleted successfully", null), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting ticket with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error deleting ticket: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
