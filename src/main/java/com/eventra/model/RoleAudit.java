package com.eventra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role_audit")
@Getter
@Setter
public class RoleAudit extends AuditEntity {
    // No additional fields needed for basic audit, but can be added if specific role-related audit data is required.
}
