package com.eventra.service;

import com.eventra.dto.TicketRequest;
import com.eventra.dto.TicketResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.dto.EventResponse;
import com.eventra.dto.TicketRequest;
import com.eventra.dto.TicketResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Event;
import com.eventra.model.Ticket;
import com.eventra.repository.EventRepository;
import com.eventra.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    public TicketResponse createTicket(TicketRequest ticketRequest) {
        Event event = eventRepository.findById(ticketRequest.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + ticketRequest.getEventId()));

        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setTicketCategory(ticketRequest.getTicketCategory());
        ticket.setPrice(ticketRequest.getPrice());
        ticket.setQuota(ticketRequest.getQuota());
        // Set createdBy from authenticated user if available
        ticket.setCreatedBy("admin"); // Placeholder for now

        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse getTicketById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
        return mapToResponse(ticket);
    }

    public TicketResponse updateTicket(UUID id, TicketRequest ticketRequest) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

        Event event = eventRepository.findById(ticketRequest.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + ticketRequest.getEventId()));

        ticket.setEvent(event);
        ticket.setTicketCategory(ticketRequest.getTicketCategory());
        ticket.setPrice(ticketRequest.getPrice());
        ticket.setQuota(ticketRequest.getQuota());
        // Set updatedBy from authenticated user if available
        ticket.setUpdatedBy("admin"); // Placeholder for now

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }

    public void deleteTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
        ticketRepository.delete(ticket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        
        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(ticket.getEvent().getId());
        eventResponse.setTitle(ticket.getEvent().getTitle()); // Use getTitle() instead of getName()
        // Copy other relevant fields from Event to EventResponse if needed
        response.setEvent(eventResponse);

        response.setTicketCategory(ticket.getTicketCategory());
        response.setPrice(ticket.getPrice());
        response.setQuota(ticket.getQuota());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setCreatedBy(ticket.getCreatedBy());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setUpdatedBy(ticket.getUpdatedBy());
        return response;
    }
}
