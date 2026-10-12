package com.slick.lead_revenue_automation_system.entities;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name = "quote_item")
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "service_id")
    private Service service;
    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;
}