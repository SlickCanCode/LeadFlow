package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name = "follow_up")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowUp {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;
    @Enumerated(EnumType.STRING) @Column(name = "channel", nullable = false, length = 20)
    private MessageChannel channel;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20)
    private FollowUpStatus status = FollowUpStatus.PENDING;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "message_template_id")
    private MessageTemplate messageTemplate;
    @Column(name = "completed_at")
    private Instant completedAt;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
}