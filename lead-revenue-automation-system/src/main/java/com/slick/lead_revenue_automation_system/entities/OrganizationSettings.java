package com.slick.lead_revenue_automation_system.entities;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.AITone;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

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
@Table(name = "organization_settings")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, unique = true)
    private Organization organization;

    @Column(name = "business_description", columnDefinition = "text")
    private String businessDescription;

    @Column(name = "ai_instructions", columnDefinition = "text")
    private String aiInstructions;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "ai_tone", nullable = false, length = 30)
    private AITone aiTone = AITone.PROFESSIONAL;

    @Column(name = "auto_reply_enabled", nullable = false)
    private boolean autoReplyEnabled;

    @Column(name = "require_human_approval", nullable = false)
    private boolean requireHumanApproval = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", nullable = false)
    private Map<String, Object> settings = Map.of();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    public OrganizationSettings(Organization organization, String businessDescription, String aiInstructions,
            AITone aiTone, boolean autoReplyEnabled, boolean requireHumanApproval, Map<String, Object> settings) {
        this.organization = organization;
        this.businessDescription = businessDescription;
        this.aiInstructions = aiInstructions;
        this.aiTone = aiTone == null ? AITone.PROFESSIONAL : aiTone;
        this.autoReplyEnabled = autoReplyEnabled;
        this.requireHumanApproval = requireHumanApproval;
        this.settings = settings == null ? Map.of() : settings;
    }
}