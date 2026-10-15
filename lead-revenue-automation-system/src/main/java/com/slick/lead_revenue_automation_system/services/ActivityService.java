package com.slick.lead_revenue_automation_system.services;

import java.util.List;
import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;
import com.slick.lead_revenue_automation_system.services.context.ActivityConversationInfo;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;

public interface ActivityService {
    List<ActivityConversationInfo> getRecentActivities(UUID organizationId, UUID leadId);

    void recordMessage(UUID organizationId, LeadConversationInfo lead, ActivityType type,
            ActivityDirection direction, String content);
}