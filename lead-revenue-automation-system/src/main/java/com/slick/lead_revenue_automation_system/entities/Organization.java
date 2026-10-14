package com.slick.lead_revenue_automation_system.entities;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organization", uniqueConstraints = @UniqueConstraint(name = "uq_organization_slug", columnNames = "slug"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "slug", nullable = false, length = 150)
    private String slug;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone = "UTC";

    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "char(3)")
    private String currency = "USD";

    @OneToOne(mappedBy = "organization", fetch = FetchType.LAZY)
    private OrganizationSettings organizationSettings;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    public Organization(String name, String slug, String email, String phone, String address,
            String logoUrl, String timezone, String currency) {
        this.name = name;
        this.slug = slug;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.logoUrl = logoUrl;
        this.timezone = timezone == null ? "UTC" : timezone;
        this.currency = currency == null ? "USD" : currency;
    }
}