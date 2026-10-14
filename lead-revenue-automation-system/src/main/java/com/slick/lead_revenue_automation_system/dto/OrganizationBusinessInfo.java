package com.slick.lead_revenue_automation_system.dto;

import java.util.Map;
import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.AITone;

public record OrganizationBusinessInfo(
        UUID id,
        String name,
        String email,
        String phone,
        String address,
        String timezone,
        String currency,
        String businessDescription,
        String aiInstructions,
        AITone aiTone,
        boolean autoReplyEnabled,
        boolean requireHumanApproval,
        Map<String, Object> settings) {
}