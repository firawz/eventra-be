package com.eventra.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(name = "StartDate", nullable = false)
    private OffsetDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private OffsetDateTime endDate;

    @Column(name = "CreatedAt", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedAt")
    private OffsetDateTime updatedAt;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // user_id as foreign key, optional
    private User user;

    @Column(name = "ImageUrl")
    private String imageUrl;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
