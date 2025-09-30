package com.eventra.service;

import com.eventra.dto.EventRequest;
import com.eventra.dto.EventResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.model.Event;
import com.eventra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.criteria.Predicate;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public PaginationResponse<EventResponse> getAllEvents(
            int page, int limit,
            String title, String description, String sortByDate) {

        Sort sort = Sort.by("createdAt").descending(); // Default sort
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
    }


    public Optional<EventResponse> getEventById(UUID id) {
        return eventRepository.findById(id)
                .map(this::convertToDto);
    }

    public EventResponse createEvent(EventRequest eventRequest) {
        Event event = new Event();
        event.setTitle(eventRequest.getTitle());
        event.setDescription(eventRequest.getDescription());
        event.setLocation(eventRequest.getLocation());
        event.setStartDate(eventRequest.getStartDate());
        event.setEndDate(eventRequest.getEndDate());
        event.setCreatedBy(eventRequest.getCreatedBy());
        event.setCreatedAt(LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);
        return convertToDto(savedEvent);
    }

    public Optional<EventResponse> updateEvent(UUID id, EventRequest eventRequest) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setTitle(eventRequest.getTitle());
                    existingEvent.setDescription(eventRequest.getDescription());
                    existingEvent.setLocation(eventRequest.getLocation());
                    existingEvent.setStartDate(eventRequest.getStartDate());
                    existingEvent.setEndDate(eventRequest.getEndDate());
                    existingEvent.setUpdatedBy(eventRequest.getUpdatedBy());
                    existingEvent.setUpdatedAt(LocalDateTime.now());
                    Event updatedEvent = eventRepository.save(existingEvent);
                    return convertToDto(updatedEvent);
                });
    }

    public boolean deleteEvent(UUID id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
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
        return eventResponse;
    }
}
