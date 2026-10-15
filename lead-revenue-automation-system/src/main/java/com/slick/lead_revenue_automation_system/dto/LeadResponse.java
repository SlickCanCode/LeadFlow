package com.slick.lead_revenue_automation_system.dto;

import java.time.Instant;
import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;
import com.slick.lead_revenue_automation_system.entities.enums.LeadStatus;

public record LeadResponse(
        UUID id,
        UUID organizationId,
        String externalId,
        String firstName,
        String lastName,
        String email,
        String phone,
        LeadSource source,
        LeadStatus status,
        Instant createdAt) {
}