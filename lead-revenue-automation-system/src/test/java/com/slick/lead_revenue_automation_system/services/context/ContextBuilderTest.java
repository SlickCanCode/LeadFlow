package com.slick.lead_revenue_automation_system.services.context;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;

class ContextBuilderTest {
    private final ContextBuilder contextBuilder = new ContextBuilder();

    @Test
    void buildsContextWithRequiredOrganizationAndOptionalData() {
        OrganizationBusinessInfo businessInfo = org.mockito.Mockito.mock(OrganizationBusinessInfo.class);

        ConversationContext context = contextBuilder.build(businessInfo, null, null, null);

        assertSame(businessInfo, context.organization());
        assertNull(context.lead());
        assertNull(context.recentActivities());
        assertNull(context.businessPolicies());
    }
}