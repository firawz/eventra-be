package com.eventra.repository;

import com.eventra.model.OrderAudit;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAuditRepository extends AuditRepository<OrderAudit> {
}
