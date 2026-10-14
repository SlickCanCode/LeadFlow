package com.slick.lead_revenue_automation_system.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.AITone;

public record OrganizationSettingsResponse(
        UUID id,
        UUID organizationId,
        String businessDescription,
        String aiInstructions,
        AITone aiTone,
        boolean autoReplyEnabled,
        boolean requireHumanApproval,
        Map<String, Object> settings,
        Instant createdAt,
        Instant updatedAt) {
}