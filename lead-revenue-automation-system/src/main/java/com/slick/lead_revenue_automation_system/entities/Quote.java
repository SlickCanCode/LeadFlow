package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "quote", uniqueConstraints = @UniqueConstraint(name = "uq_quote_number", columnNames = { "organization_id", "quote_number" }))
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    @Column(name = "quote_number", nullable = false, length = 50)
    private String quoteNumber;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20)
    private QuoteStatus status = QuoteStatus.DRAFT;
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(name = "discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;
    @Column(name = "tax", nullable = false, precision = 12, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "char(3)")
    private String currency;
    @Column(name = "valid_until")
    private LocalDate validUntil;
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    @Column(name = "sent_at")
    private Instant sentAt;
    @Column(name = "responded_at")
    private Instant respondedAt;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
}