package com.eventra.repository;

import com.eventra.model.EventAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventAuditRepository extends JpaRepository<EventAudit, UUID> {
}
