package com.eventra.service;

public interface AuditService {
    void publishCreateAudit(Object entity, String action);
    void publishUpdateAudit(Object entity, String action);
    void publishDeleteAudit(Object entity, String action);
}
