package com.slick.lead_revenue_automation_system.dto;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String slug,
        String email,
        String phone,
        String address,
        String logoUrl,
        String timezone,
        String currency,
        Instant createdAt,
        Instant updatedAt) {
}