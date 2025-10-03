package com.eventra.event;

import org.springframework.context.ApplicationEvent;

public class AuditCreatedEvent extends ApplicationEvent {
    private final Object entity;
    private final String action;

    public AuditCreatedEvent(Object source, Object entity, String action) {
        super(source);
        this.entity = entity;
        this.action = action;
    }

    public Object getEntity() {
        return entity;
    }

    public String getAction() {
        return action;
    }
}
