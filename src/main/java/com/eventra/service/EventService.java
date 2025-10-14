package com.eventra.service;

import com.eventra.dto.EventRequest;
import com.eventra.dto.EventResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.model.Event;
import com.eventra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.eventra.dto.TicketRequest;
import com.eventra.dto.TicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eventra.config.CustomUserDetails;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private TicketService ticketService;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public PaginationResponse<EventResponse> getAllEvents(
            int page, int limit,
            String title, String description, String category, String status, String sortByDate) {
        try {
            Sort sort = Sort.by("startDate").ascending(); // Default sort
            if (sortByDate != null && !sortByDate.isEmpty()) {
                if (sortByDate.equalsIgnoreCase("asc")) {
                    sort = Sort.by("startDate").ascending();
                } else if (sortByDate.equalsIgnoreCase("desc")) {
                    sort = Sort.by("startDate").descending();
                }
            }

            Pageable pageable = PageRequest.of(page - 1, limit, sort);

            Specification<Event> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (title != null && !title.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
                }
                if (description != null && !description.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
                }
                if (category != null && !category.isEmpty()) {
                    predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
                }
                if (status != null && !status.isEmpty()) {
                    predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };

            Page<Event> eventPage = eventRepository.findAll(spec, pageable);

            List<EventResponse> content = eventPage.getContent().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new PaginationResponse<>(
                    content,
                    eventPage.getNumber() + 1,
                    eventPage.getSize(),
                    eventPage.getTotalElements(),
                    eventPage.getTotalPages(),
                    eventPage.isLast()
            );
        } catch (Exception e) {
            logger.error("Error retrieving all events: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving all events: " + e.getMessage());
        }
    }


    public Optional<EventResponse> getEventById(UUID id) {
        try {
            return eventRepository.findById(id)
                    .map(this::convertToDto);
        } catch (Exception e) {
            logger.error("Error retrieving event by ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error retrieving event by ID: " + e.getMessage());
        }
    }

    public EventResponse createEvent(EventRequest eventRequest) {
        try {
            Event event = new Event();
            event.setTitle(eventRequest.getTitle());
            event.setDescription(eventRequest.getDescription());
            event.setLocation(eventRequest.getLocation());
            event.setStartDate(eventRequest.getStartDate());
            event.setEndDate(eventRequest.getEndDate());
            event.setImageUrl(eventRequest.getImageUrl());
            event.setCategory(eventRequest.getCategory());
            event.setStatus(eventRequest.getStatus());
            event.setCreatedAt(OffsetDateTime.now());
            event.setUpdatedAt(OffsetDateTime.now());
            event.setCreatedBy(getCurrentAuditor()); // Set createdBy from security context
			event.setUpdatedBy(getCurrentAuditor());
            Event savedEvent = eventRepository.save(event);

            // Create tickets if provided
            if (eventRequest.getTickets() != null && !eventRequest.getTickets().isEmpty()) {
                for (TicketRequest ticketRequest : eventRequest.getTickets()) {
                    ticketRequest.setEventId(savedEvent.getId()); // Associate ticket with the newly created event
                    ticketService.createTicket(ticketRequest);
                }
            }
            auditService.publishCreateAudit(savedEvent, "CREATE"); // Use new audit method

            return convertToDto(savedEvent);
        } catch (Exception e) {
            logger.error("Error creating event: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating event: " + e.getMessage());
        }
    }

    @Transactional
    public Optional<EventResponse> updateEvent(UUID id, EventRequest eventRequest) {
        try {
            return eventRepository.findById(id)
                    .map(existingEvent -> {
                        if (eventRequest.getTitle() != null) {
                            existingEvent.setTitle(eventRequest.getTitle());
                        }
                        if (eventRequest.getDescription() != null) {
                            existingEvent.setDescription(eventRequest.getDescription());
                        }
                        if (eventRequest.getLocation() != null) {
                            existingEvent.setLocation(eventRequest.getLocation());
                        }
                        if (eventRequest.getStartDate() != null) {
                            existingEvent.setStartDate(eventRequest.getStartDate());
                        }
                        if (eventRequest.getEndDate() != null) {
                            existingEvent.setEndDate(eventRequest.getEndDate());
                        }
                        if (eventRequest.getImageUrl() != null) {
                            existingEvent.setImageUrl(eventRequest.getImageUrl());
                        }
                        if (eventRequest.getCategory() != null) {
                            existingEvent.setCategory(eventRequest.getCategory());
                        }
                        if (eventRequest.getStatus() != null) {
                            existingEvent.setStatus(eventRequest.getStatus());
                        }
                        existingEvent.setUpdatedAt(OffsetDateTime.now());
                        existingEvent.setUpdatedBy(getCurrentAuditor()); // Set updatedBy from security context
                        Event updatedEvent = eventRepository.save(existingEvent);
                        auditService.publishUpdateAudit(updatedEvent, "UPDATE"); // Use new audit method
                        return convertToDto(updatedEvent);
                    });
        } catch (Exception e) {
            logger.error("Error updating event with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error updating event: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteEvent(UUID id) {
        try {
            Optional<Event> eventOptional = eventRepository.findById(id);
            if (eventOptional.isPresent()) {
                Event eventToDelete = eventOptional.get();
                eventRepository.deleteById(id);
                auditService.publishDeleteAudit(eventToDelete, "DELETE"); // Use new audit method
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error deleting event with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error deleting event: " + e.getMessage());
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

    private EventResponse convertToDto(Event event) {
        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(event.getId());
        eventResponse.setTitle(event.getTitle());
        eventResponse.setDescription(event.getDescription());
        eventResponse.setLocation(event.getLocation());
        eventResponse.setStartDate(event.getStartDate());
        eventResponse.setEndDate(event.getEndDate());
        eventResponse.setCreatedAt(event.getCreatedAt());
        eventResponse.setCreatedBy(event.getCreatedBy());
        eventResponse.setUpdatedAt(event.getUpdatedAt());
        eventResponse.setUpdatedBy(event.getUpdatedBy());
        eventResponse.setImageUrl(event.getImageUrl());
        eventResponse.setCategory(event.getCategory());
        eventResponse.setStatus(event.getStatus());

        // Fetch and convert tickets
        List<TicketResponse> ticketResponses = ticketService.getTicketsByEventId(event.getId());
        eventResponse.setTickets(ticketResponses);

        return eventResponse;
    }
}
