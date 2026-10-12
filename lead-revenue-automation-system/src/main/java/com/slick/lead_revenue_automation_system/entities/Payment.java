package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "payment", uniqueConstraints = @UniqueConstraint(name = "uq_payment_provider_reference", columnNames = { "provider", "provider_reference" }))
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "quote_id")
    private Quote quote;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "booking_id")
    private Booking booking;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "char(3)")
    private String currency;
    @Column(name = "provider", nullable = false, length = 50)
    private String provider;
    @Column(name = "provider_reference", length = 255)
    private String providerReference;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name = "paid_at")
    private Instant paidAt;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
}