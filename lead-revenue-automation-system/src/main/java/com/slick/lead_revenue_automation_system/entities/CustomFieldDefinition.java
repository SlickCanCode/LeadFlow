package com.slick.lead_revenue_automation_system.entities;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.slick.lead_revenue_automation_system.entities.enums.CustomFieldDataType;
import com.slick.lead_revenue_automation_system.entities.enums.CustomFieldEntityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "custom_field_definitions", uniqueConstraints = @UniqueConstraint(
        name = "uq_custom_field_key", columnNames = { "organization_id", "entity_type", "field_key" }))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomFieldDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 20)
    private CustomFieldEntityType entityType;

    @Column(name = "field_key", nullable = false, length = 100)
    private String fieldKey;

    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 30)
    private CustomFieldDataType dataType;

    @Column(name = "required", nullable = false)
    private boolean required;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options", nullable = false)
    private List<Object> options = List.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "validation_rules", nullable = false)
    private Map<String, Object> validationRules = Map.of();

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}