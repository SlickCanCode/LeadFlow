package com.slick.lead_revenue_automation_system.entities;

import java.math.BigDecimal;
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
@Table(name = "quote_policies")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuotePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, unique = true)
    private Organization organization;

    @Column(name = "require_inspection", nullable = false)
    private boolean requireInspection;

    @Column(name = "require_approval", nullable = false)
    private boolean requireApproval = true;

    @Column(name = "default_validity_days", nullable = false)
    private int defaultValidityDays = 7;

    @Column(name = "default_tax_rate", nullable = false, precision = 7, scale = 4)
    private BigDecimal defaultTaxRate = BigDecimal.ZERO;

    @Column(name = "allow_discounts", nullable = false)
    private boolean allowDiscounts;

    @Column(name = "max_discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxDiscountPercent = BigDecimal.ZERO;

    @Column(name = "terms_and_conditions", columnDefinition = "text")
    private String termsAndConditions;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}