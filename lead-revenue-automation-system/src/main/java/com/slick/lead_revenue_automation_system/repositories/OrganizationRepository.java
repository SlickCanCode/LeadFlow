package com.slick.lead_revenue_automation_system.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slick.lead_revenue_automation_system.entities.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsBySlug(String slug);
}