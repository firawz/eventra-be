package com.eventra.repository;

import com.eventra.model.TicketAudit;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAuditRepository extends AuditRepository<TicketAudit> {
}
