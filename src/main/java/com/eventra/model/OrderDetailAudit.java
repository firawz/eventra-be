package com.eventra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
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

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "ticket_code", nullable = false)
    private String ticketCode;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
