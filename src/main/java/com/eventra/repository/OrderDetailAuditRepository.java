package com.eventra.repository;

import com.eventra.model.OrderDetailAudit;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailAuditRepository extends AuditRepository<OrderDetailAudit> {
}
