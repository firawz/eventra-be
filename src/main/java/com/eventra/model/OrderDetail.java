package com.eventra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    @Column(name = "id")
    private UUID Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order Order;

    @Column(name = "nik", nullable = false)
    private String Nik;

    @Column(name = "full_name", nullable = false)
    private String FullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate BirthDate;

    @Column(name = "ticket_code", nullable = false)
    private String TicketCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime CreatedAt;

    @Column(name = "created_by")
    private String CreatedBy;

    @Column(name = "updated_at")
    private LocalDateTime UpdatedAt;

    @Column(name = "updated_by")
    private String UpdatedBy;

    @PrePersist
    protected void onCreate() {
        this.CreatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.UpdatedAt = LocalDateTime.now();
    }
}
