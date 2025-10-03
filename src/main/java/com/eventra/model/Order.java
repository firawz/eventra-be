package com.eventra.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @OneToMany(mappedBy = "Order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> OrderDetails = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventId", nullable = false)
    private Event event;

    @Column(name = "Status", nullable = false)
    private String status; // e.g., "PENDING", "COMPLETED", "CANCELLED"

    @Column(name = "TotalPrice", nullable = false)
    private Double totalPrice;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
