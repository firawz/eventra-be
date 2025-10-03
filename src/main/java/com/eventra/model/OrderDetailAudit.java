package com.eventra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "order_detail_audit")
@Getter
@Setter
public class OrderDetailAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AuditId", updatable = false, nullable = false)
    private UUID auditId;

    @Column(name = "Action", nullable = false, updatable = false)
    private String action;

    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "OrderDetailId", nullable = false)
    private UUID orderDetailId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "nik", nullable = false)
    private String nik;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    @Column(name = "ticket_code", nullable = false)
    private String ticketCode;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
