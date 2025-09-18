package com.eventra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket_audit")
@Getter
@Setter
public class TicketAudit extends AuditEntity {
    // No additional fields needed for basic audit, but can be added if specific ticket-related audit data is required.
}
