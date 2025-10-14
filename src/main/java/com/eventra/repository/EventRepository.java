package com.eventra.repository;

import com.eventra.model.Event;
import com.eventra.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
    Page<Event> findByIdIn(Set<UUID> ids, Pageable pageable);
    Page<Event> findByIdInAndStatus(Set<UUID> ids, EventStatus status, Pageable pageable);
}
