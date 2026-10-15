package com.slick.lead_revenue_automation_system.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WebChatMessageRequest(
        @NotNull(message = "Organization ID is required") UUID organizationId,
        @NotBlank(message = "Lead external ID is required")
        @Size(max = 255, message = "Lead external ID cannot exceed 255 characters") String externalId,
        @NotBlank(message = "Message is required") String message) {
    public WebChatMessageRequest {
        externalId = trimToNull(externalId);
        message = message == null ? null : message.trim();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}