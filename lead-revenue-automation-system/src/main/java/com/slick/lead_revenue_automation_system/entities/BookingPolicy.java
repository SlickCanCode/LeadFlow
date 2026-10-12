package com.slick.lead_revenue_automation_system.entities;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "booking_policies")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, unique = true)
    private Organization organization;

    @Column(name = "booking_enabled", nullable = false)
    private boolean bookingEnabled = true;

    @Column(name = "slot_duration_minutes", nullable = false)
    private int slotDurationMinutes = 60;

    @Column(name = "buffer_minutes", nullable = false)
    private int bufferMinutes;

    @Column(name = "minimum_notice_minutes", nullable = false)
    private int minimumNoticeMinutes = 60;

    @Column(name = "maximum_advance_days", nullable = false)
    private int maximumAdvanceDays = 30;

    @Column(name = "require_confirmation", nullable = false)
    private boolean requireConfirmation = true;

    @Column(name = "cancellation_notice_hours", nullable = false)
    private int cancellationNoticeHours = 24;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}