package com.eventra.repository;

import com.eventra.model.UserAudit;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuditRepository extends AuditRepository<UserAudit> {
}
