package com.slick.lead_revenue_automation_system.services.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.slick.lead_revenue_automation_system.services.OrganizationService;

@Component
public class ContextBuilder {
    private final OrganizationService organizationService;

    public ContextBuilder(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public ConversationContext build(UUID organizationId) {
        return build(organizationId, null, null, null);
    }

    public ConversationContext build(UUID organizationId, Map<String, Object> leadDetails,
            List<Map<String, Object>> recentActivities, Map<String, Object> businessPolicies) {
        return new ConversationContext(
                organizationService.getOrganizationBusinessInfo(organizationId),
                leadDetails,
                recentActivities,
                businessPolicies);
    }
}