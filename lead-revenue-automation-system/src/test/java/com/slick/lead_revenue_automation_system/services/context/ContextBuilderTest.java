package com.slick.lead_revenue_automation_system.services.context;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.services.OrganizationService;

@ExtendWith(MockitoExtension.class)
class ContextBuilderTest {
    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private ContextBuilder contextBuilder;

    @Test
    void buildsOrganizationContextWithUnavailableDataLeftNull() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        OrganizationBusinessInfo businessInfo = org.mockito.Mockito.mock(OrganizationBusinessInfo.class);
        when(organizationService.getOrganizationBusinessInfo(organizationId)).thenReturn(businessInfo);

        ConversationContext context = contextBuilder.build(organizationId);

        assertSame(businessInfo, context.organization());
        assertNull(context.leadDetails());
        assertNull(context.recentActivities());
        assertNull(context.businessPolicies());
        verify(organizationService).getOrganizationBusinessInfo(organizationId);
    }
}