package com.slick.lead_revenue_automation_system.services.context;

import java.util.Map;
import java.util.List;

import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;

public record ConversationContext(
        OrganizationBusinessInfo organization,
        LeadConversationInfo lead,
        List<ActivityConversationInfo> recentActivities,
        Map<String, Object> businessPolicies) {
}