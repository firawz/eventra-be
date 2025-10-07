package com.eventra.repository;

import com.eventra.model.RoleAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleAuditRepository extends JpaRepository<RoleAudit, UUID> {
}
