package com.slick.lead_revenue_automation_system.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slick.lead_revenue_automation_system.entities.Activity;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findTop8ByOrganization_IdAndLead_IdOrderByOccurredAtDescIdDesc(UUID organizationId, UUID leadId);
}