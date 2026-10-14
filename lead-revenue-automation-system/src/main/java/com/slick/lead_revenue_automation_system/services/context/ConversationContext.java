package com.slick.lead_revenue_automation_system.services.context;

import java.util.List;
import java.util.Map;

import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;

public record ConversationContext(
        OrganizationBusinessInfo organization,
        Map<String, Object> leadDetails,
        List<Map<String, Object>> recentActivities,
        Map<String, Object> businessPolicies) {
}