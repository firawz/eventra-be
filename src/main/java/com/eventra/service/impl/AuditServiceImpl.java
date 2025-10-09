package com.eventra.service.impl;

import com.eventra.event.AuditCreatedEvent;
import com.eventra.event.AuditDeletedEvent;
import com.eventra.event.AuditUpdatedEvent;
import com.eventra.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void publishCreateAudit(Object entity, String action) {
        eventPublisher.publishEvent(new AuditCreatedEvent(this, entity, action));
    }

    @Override
    public void publishUpdateAudit(Object entity, String action) {
        eventPublisher.publishEvent(new AuditUpdatedEvent(this, entity, action));
    }

    @Override
    public void publishDeleteAudit(Object entity, String action) {
        eventPublisher.publishEvent(new AuditDeletedEvent(this, entity, action));
    }
}
