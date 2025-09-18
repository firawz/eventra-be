package com.eventra.service.impl;

import com.eventra.model.AuditEntity;
import com.eventra.repository.AuditRepository;
import com.eventra.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public abstract class AuditServiceImpl<T extends AuditEntity> implements AuditService<T> {

    private final AuditRepository<T> auditRepository;

    @Autowired
    public AuditServiceImpl(AuditRepository<T> auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void createAudit(T auditEntity) {
        auditRepository.save(auditEntity);
    }
}
