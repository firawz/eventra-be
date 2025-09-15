package com.eventra.service;

import com.eventra.dto.EventRequest;
import com.eventra.dto.EventResponse;
import com.eventra.model.Event;
import com.eventra.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public List<EventResponse> getAllEvents(
        // LocalDate date,
        String title, String description, String sortByDate) {
        // LocalDate parsedDate = null;
        // if (date != null) {
        //     try {
        //         parsedDate = LocalDate.parse(date.toString());
        //     } catch (DateTimeParseException e) {
        //         logger.warn("Invalid date format: {}", date, e);
        //         // Handle invalid date format, perhaps return an empty list or throw a custom exception
        //         // For now, we'll proceed with parsedDate as null, meaning no date filter will be applied
        //     }
        // }

        List<Event> events;

        if (title != null && !title.isEmpty() && description != null && !description.isEmpty()) {
            events = eventRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(title, description);
        } else if (title != null && !title.isEmpty()) {
            events = eventRepository.findByTitleContainingIgnoreCase(title);
        } else if (description != null && !description.isEmpty()) {
            events = eventRepository.findByDescriptionContainingIgnoreCase(description);
        } else {
            events = eventRepository.findAll(); // Return all events if no search parameters
        }

        if (sortByDate != null && !sortByDate.isEmpty()) {
            if (sortByDate.equalsIgnoreCase("asc")) {
                events.sort((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()));
            } else if (sortByDate.equalsIgnoreCase("desc")) {
                events.sort((e1, e2) -> e2.getStartDate().compareTo(e1.getStartDate()));
            }
        }

        return events.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EventResponse> searchEvents(String keyword) {
        return eventRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
