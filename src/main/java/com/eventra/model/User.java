package com.eventra.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users") // Renamed to 'users' to avoid conflict with SQL keyword 'user'
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "FullName", nullable = false)
    private String fullName;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Role", nullable = false)
    private String role;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "Gender")
    private String gender; // e.g., "MALE", "FEMALE", "OTHER"

    @Column(name = "Nik", unique = true)
    private String nik; // National Identity Number

    @Column(name = "IsRegistered", nullable = false)
    private Boolean isRegistered;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    // Constructors
    // Lombok's @NoArgsConstructor handles the default constructor.
    // We can initialize default values in the field declaration or a custom constructor if needed.
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRegistered == null) {
            isRegistered = false;
        }
    }
}
