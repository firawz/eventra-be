package com.eventra.repository;

import com.eventra.model.OrderDetailAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderDetailAuditRepository extends JpaRepository<OrderDetailAudit, UUID> {
}
