package com.eventra.service;

public interface AuditService {
    void publishAudit(Object entity, String action);
}
