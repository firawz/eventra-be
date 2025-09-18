package com.eventra.service;

import com.eventra.model.AuditEntity;

public interface AuditService<T extends AuditEntity> {
    void createAudit(T auditEntity);
}
