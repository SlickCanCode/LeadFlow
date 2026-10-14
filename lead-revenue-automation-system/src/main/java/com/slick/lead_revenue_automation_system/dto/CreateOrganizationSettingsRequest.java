package com.slick.lead_revenue_automation_system.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.slick.lead_revenue_automation_system.entities.enums.AITone;

public record CreateOrganizationSettingsRequest(
        String businessDescription,
        String aiInstructions,
        AITone aiTone,
        Boolean autoReplyEnabled,
        Boolean requireHumanApproval,
        Map<String, Object> settings) {
    public CreateOrganizationSettingsRequest {
        businessDescription = trimToNull(businessDescription);
        aiInstructions = trimToNull(aiInstructions);
        aiTone = aiTone == null ? AITone.PROFESSIONAL : aiTone;
        autoReplyEnabled = Boolean.TRUE.equals(autoReplyEnabled);
        requireHumanApproval = requireHumanApproval == null || requireHumanApproval;
        settings = settings == null ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(settings));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}