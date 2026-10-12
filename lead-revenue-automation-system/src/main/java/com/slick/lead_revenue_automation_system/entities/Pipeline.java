package com.slick.lead_revenue_automation_system.entities;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name = "pipeline")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pipeline {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}