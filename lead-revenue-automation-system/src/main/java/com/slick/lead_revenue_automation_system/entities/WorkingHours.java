package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.time.LocalTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "working_hours", uniqueConstraints = @UniqueConstraint(name = "uq_working_hours_day", columnNames = { "organization_id", "day_of_week" }))
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkingHours {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Enumerated(EnumType.STRING) @Column(name = "day_of_week", nullable = false, length = 10)
    private WorkingDay dayOfWeek;
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;
}