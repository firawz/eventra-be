package com.eventra.service;

import com.eventra.dto.TicketRequest;
import com.eventra.dto.PaginationResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.eventra.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eventra.config.CustomUserDetails;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuditService auditService;

    public TicketResponse createTicket(TicketRequest ticketRequest) {
        try {
            Event event = eventRepository.findById(ticketRequest.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + ticketRequest.getEventId()));

            Ticket ticket = new Ticket();
            ticket.setEvent(event);
            ticket.setTicketCategory(ticketRequest.getTicketCategory());
            ticket.setPrice(ticketRequest.getPrice());
            ticket.setQuota(ticketRequest.getQuota());
            ticket.setCreatedBy(getCurrentAuditor()); // Set createdBy from security context

            Ticket savedTicket = ticketRepository.save(ticket);
            auditService.publishCreateAudit(savedTicket, "CREATE"); // Use new audit method
            return mapToResponse(savedTicket);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during createTicket: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating ticket: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating ticket: " + e.getMessage());
        }
    }

    public PaginationResponse<TicketResponse> getAllTickets(int page, int limit) {
        try {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
            Page<Ticket> ticketPage = ticketRepository.findAll(pageable);

            List<TicketResponse> content = ticketPage.getContent().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return new PaginationResponse<>(
                    content,
                    ticketPage.getNumber() + 1,
                    ticketPage.getSize(),
                    ticketPage.getTotalElements(),
                    ticketPage.getTotalPages(),
                    ticketPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error retrieving all tickets: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving all tickets: " + e.getMessage());
        }
    }

    public TicketResponse getTicketById(UUID id) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
            return mapToResponse(ticket);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during getTicketById for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving ticket by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving ticket by ID: " + e.getMessage());
        }
    }

    public TicketResponse updateTicket(UUID id, TicketRequest ticketRequest) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));

            Event event = eventRepository.findById(ticketRequest.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + ticketRequest.getEventId()));

            ticket.setEvent(event);
            ticket.setTicketCategory(ticketRequest.getTicketCategory());
            ticket.setPrice(ticketRequest.getPrice());
            ticket.setQuota(ticketRequest.getQuota());
            ticket.setUpdatedBy(getCurrentAuditor()); // Set updatedBy from security context

            Ticket updatedTicket = ticketRepository.save(ticket);
            auditService.publishUpdateAudit(updatedTicket, "UPDATE"); // Use new audit method
            return mapToResponse(updatedTicket);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during updateTicket for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating ticket with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating ticket: " + e.getMessage());
        }
    }

    public void deleteTicket(UUID id) {
        try {
            Ticket ticket = ticketRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
            ticketRepository.delete(ticket);
            auditService.publishDeleteAudit(ticket, "DELETE"); // Use new audit method
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during deleteTicket for ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting ticket with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting ticket: " + e.getMessage());
        }
    }

    private String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) principal;
                return customUserDetails.getUserId().toString();
            } else {
                logger.warn("Principal is not CustomUserDetails. Returning 'SYSTEM' for auditor.");
                return "SYSTEM";
            }
        }
        return "SYSTEM";
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
