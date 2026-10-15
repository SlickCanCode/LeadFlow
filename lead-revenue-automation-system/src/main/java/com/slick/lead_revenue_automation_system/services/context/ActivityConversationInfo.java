package com.slick.lead_revenue_automation_system.services.context;

import java.time.Instant;

import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;

public record ActivityConversationInfo(
        ActivityType type,
        ActivityDirection direction,
        String subject,
        String content,
        Instant occurredAt) {
}