package com.eventra.service;

import com.eventra.dto.EventRequest;
import com.eventra.dto.EventResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.model.Event;
import com.eventra.repository.EventRepository;
import com.eventra.repository.OrderDetailRepository;
import com.eventra.repository.OrderRepository;
import com.eventra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.eventra.dto.TicketRequest;
import com.eventra.dto.TicketResponse;
import com.eventra.dto.SummaryResponse;
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
import com.eventra.model.EventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eventra.config.CustomUserDetails;
import com.eventra.model.enums.OrderStatus;
import com.eventra.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.math.BigDecimal;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.eventra.model.EventStatus;
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

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
                    try {
                        EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
                        predicates.add(cb.equal(root.get("status"), eventStatus));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid status provided: {}", status);
                    }
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
            event.setStatus(eventRequest.getStatus().toString());
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
                            existingEvent.setStatus(eventRequest.getStatus().toString());
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
        eventResponse.setStatus(event.getStatus().toString()); // Convert enum to string

        // Fetch and convert tickets
        List<TicketResponse> ticketResponses = ticketService.getTicketsByEventId(event.getId());
        eventResponse.setTickets(ticketResponses);

        return eventResponse;
    }

    public SummaryResponse getSummaryData() {
        // Total count of orderDetail
        Integer totalOrderDetails = Math.toIntExact(orderDetailRepository.count());

        // Total upcoming events
        OffsetDateTime now = OffsetDateTime.now();
        Integer totalUpcomingEvents = Math.toIntExact(eventRepository.countByStartDateAfter(now));

        // Sum of totalPrice from orderTable
        // Assuming Order has a 'totalPrice' field and OrderStatus enum
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<com.eventra.model.Order> orderRoot = query.from(com.eventra.model.Order.class);
        query.select(cb.sum(orderRoot.get("totalPrice")));
        Integer totalRevenue = entityManager.createQuery(query).getSingleResult();
        if (totalRevenue == null) {
            totalRevenue = 0;
        }

        // Total count of users with role "USER"
        Integer totalUsersWithUserRole = Math.toIntExact(userRepository.countByRole(Role.USER.name()));

        return new SummaryResponse(
                totalOrderDetails,
                totalUpcomingEvents,
                totalRevenue,
                totalUsersWithUserRole
        );
    }
}
