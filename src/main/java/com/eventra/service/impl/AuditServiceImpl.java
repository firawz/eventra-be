package com.eventra.service.impl;

import com.eventra.event.AuditCreatedEvent;
import com.eventra.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void publishAudit(Object entity, String action) {
        eventPublisher.publishEvent(new AuditCreatedEvent(this, entity, action));
    }
}
