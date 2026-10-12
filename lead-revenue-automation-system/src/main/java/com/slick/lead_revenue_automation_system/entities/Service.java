package com.slick.lead_revenue_automation_system.entities;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name = "service")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Service {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Column(name = "description", columnDefinition = "text")
    private String description;
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}