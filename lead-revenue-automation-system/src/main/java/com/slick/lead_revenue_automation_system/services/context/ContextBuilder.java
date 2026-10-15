package com.slick.lead_revenue_automation_system.services.context;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ContextBuilder {
    public ConversationContext build(
            com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo organization,
            LeadConversationInfo lead,
            List<ActivityConversationInfo> recentActivities,
            Map<String, Object> businessPolicies) {
        return new ConversationContext(organization, lead, recentActivities, businessPolicies);
    }
}