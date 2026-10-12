package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(name = "uq_user_organization_email", columnNames = { "organization_id", "email" }))
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(name = "role", nullable = false, length = 20)
    private UserRole role;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}