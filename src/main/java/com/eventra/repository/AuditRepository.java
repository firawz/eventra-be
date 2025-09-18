package com.eventra.repository;

import com.eventra.model.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface AuditRepository<T extends AuditEntity> extends JpaRepository<T, UUID> {
}
