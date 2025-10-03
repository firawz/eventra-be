package com.eventra.repository;

import com.eventra.model.TicketAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketAuditRepository extends JpaRepository<TicketAudit, UUID> {
}
