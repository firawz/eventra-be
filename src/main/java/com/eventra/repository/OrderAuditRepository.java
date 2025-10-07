package com.eventra.repository;

import com.eventra.model.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderAuditRepository extends JpaRepository<OrderAudit, UUID> {
}
