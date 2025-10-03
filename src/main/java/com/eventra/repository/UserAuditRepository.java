package com.eventra.repository;

import com.eventra.model.UserAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAuditRepository extends JpaRepository<UserAudit, UUID> {
}
