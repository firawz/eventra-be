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
@Table(name = "event_audit")
@Getter
@Setter
public class EventAudit {

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

    @Column(name = "EventId", nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "user_id")
    private UUID userId;
}
