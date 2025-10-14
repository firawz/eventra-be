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
@Table(name = "order_audit")
@Getter
@Setter
public class OrderAudit {

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

    @Column(name = "OrderId", nullable = false)
    private UUID orderId;

    @Column(name = "UserId", nullable = false)
    private UUID userId;

    @Column(name = "EventId", nullable = false)
    private UUID eventId;

    @Column(name = "Status", nullable = false)
    private String status;

    @Column(name = "TotalPrice", nullable = false)
    private Integer totalPrice;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "DeletedBy")
    private String deletedBy;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;
}
