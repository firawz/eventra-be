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

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@Valid @RequestBody TicketRequest ticketRequest) {
        TicketResponse ticketResponse = ticketService.createTicket(ticketRequest);
        return new ResponseEntity<>(new ApiResponse<>(true, "Ticket created successfully", ticketResponse), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<TicketResponse>>> getAllTickets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PaginationResponse<TicketResponse> tickets = ticketService.getAllTickets(page, limit);
        return new ResponseEntity<>(new ApiResponse<>(true, "Tickets retrieved successfully", tickets), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(@PathVariable UUID id) {
        TicketResponse ticketResponse = ticketService.getTicketById(id);
        return new ResponseEntity<>(new ApiResponse<>(true, "Ticket retrieved successfully", ticketResponse), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(@PathVariable UUID id, @Valid @RequestBody TicketRequest ticketRequest) {
        TicketResponse updatedTicket = ticketService.updateTicket(id, ticketRequest);
        return new ResponseEntity<>(new ApiResponse<>(true, "Ticket updated successfully", updatedTicket), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return new ResponseEntity<>(new ApiResponse<>(true, "Ticket deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}
