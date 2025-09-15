package com.eventra.repository;

import com.eventra.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
       List<Event> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword,
                     String descriptionKeyword);

       List<Event> findByTitleContainingIgnoreCase(String titleKeyword);
       List<Event> findByDescriptionContainingIgnoreCase(String descriptionKeyword);

}
