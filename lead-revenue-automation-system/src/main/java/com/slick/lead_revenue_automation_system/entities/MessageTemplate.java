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
@Entity @Table(name = "message_template")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageTemplate {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Enumerated(EnumType.STRING) @Column(name = "type", nullable = false, length = 40)
    private MessageTemplateType type;
    @Enumerated(EnumType.STRING) @Column(name = "channel", nullable = false, length = 20)
    private MessageChannel channel;
    @Column(name = "name", nullable = false, length = 150)
    private String name;
    @Column(name = "subject", length = 255)
    private String subject;
    @Column(name = "body", nullable = false, columnDefinition = "text")
    private String body;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}