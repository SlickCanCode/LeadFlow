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
@Entity @Table(name = "activity")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private AppUser user;
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false, length = 30)
    private ActivityType type;
    @Enumerated(EnumType.STRING) @Column(name = "direction", length = 20)
    private ActivityDirection direction;
    @Column(name = "subject", length = 255)
    private String subject;
    @Column(name = "content", columnDefinition = "text")
    private String content;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;

    public Activity(Lead lead, ActivityType type, ActivityDirection direction, String subject,
            String content, Instant occurredAt) {
        this.organization = lead.getOrganization();
        this.lead = lead;
        this.type = type;
        this.direction = direction;
        this.subject = subject;
        this.content = content;
        this.occurredAt = occurredAt;
    }
}