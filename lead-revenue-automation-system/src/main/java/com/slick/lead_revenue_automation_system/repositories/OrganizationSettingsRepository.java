package com.slick.lead_revenue_automation_system.repositories;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slick.lead_revenue_automation_system.entities.OrganizationSettings;

public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettings, UUID> {
    boolean existsByOrganization_Id(UUID organizationId);

    Optional<OrganizationSettings> findByOrganization_Id(UUID organizationId);
}