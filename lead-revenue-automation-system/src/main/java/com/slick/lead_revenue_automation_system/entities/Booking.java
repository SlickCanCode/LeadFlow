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
@Entity @Table(name = "booking")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_user_id")
    private AppUser assignedUser;
    @Column(name = "start_time", nullable = false)
    private Instant startTime;
    @Column(name = "end_time", nullable = false)
    private Instant endTime;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}