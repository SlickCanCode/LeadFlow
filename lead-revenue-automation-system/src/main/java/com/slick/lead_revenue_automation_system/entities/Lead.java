package com.slick.lead_revenue_automation_system.entities;

import com.slick.lead_revenue_automation_system.entities.enums.*;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity @Table(name = "lead", uniqueConstraints = @UniqueConstraint(name = "uq_lead_organization_external_id", columnNames = { "organization_id", "external_id" }))
@Getter @Setter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lead {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid") @Setter(AccessLevel.NONE)
    private UUID id;
    @Column(name = "external_id", length = 255)
    private String externalId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    @Column(name = "last_name", length = 100)
    private String lastName;
    @Column(name = "email", length = 255)
    private String email;
    @Column(name = "phone", length = 50)
    private String phone;
    @Enumerated(EnumType.STRING) @Column(name = "source", nullable = false, length = 30)
    private LeadSource source;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20)
    private LeadStatus status = LeadStatus.ACTIVE;
    @Enumerated(EnumType.STRING) @Column(name = "qualification_status", nullable = false, length = 30)
    private QualificationStatus qualificationStatus = QualificationStatus.PENDING;
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assigned_to")
    private AppUser assignedTo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pipeline_stage_id")
    private PipelineStage pipelineStage;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "lead_service", joinColumns = @JoinColumn(name = "lead_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private Set<Service> services = new HashSet<>();
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) @Setter(AccessLevel.NONE)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) @Setter(AccessLevel.NONE)
    private Instant updatedAt;
    @Column(name = "stage_changed_at")
    private Instant stageChangedAt;

    public Lead(Organization organization, String externalId, String firstName, String lastName,
            String email, String phone, LeadSource source) {
        this.organization = organization;
        this.externalId = externalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.source = source;
    }

    public Set<Service> getServices() {
        return Collections.unmodifiableSet(services);
    }
    public void addService(Service service) {
        services.add(service);
    }
    public void removeService(Service service) {
        services.remove(service);
    }
}