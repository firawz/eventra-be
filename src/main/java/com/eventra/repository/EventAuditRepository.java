package com.eventra.repository;

import com.eventra.model.EventAudit;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAuditRepository extends AuditRepository<EventAudit> {
}
