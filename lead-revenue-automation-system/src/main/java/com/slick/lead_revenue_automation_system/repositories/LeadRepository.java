package com.slick.lead_revenue_automation_system.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slick.lead_revenue_automation_system.entities.Lead;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    Optional<Lead> findByOrganization_IdAndExternalId(UUID organizationId, String externalId);

    Optional<Lead> findByOrganization_IdAndId(UUID organizationId, UUID leadId);
}