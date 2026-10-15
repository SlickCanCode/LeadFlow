package com.slick.lead_revenue_automation_system.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slick.lead_revenue_automation_system.entities.Activity;
import com.slick.lead_revenue_automation_system.entities.Lead;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;
import com.slick.lead_revenue_automation_system.repositories.ActivityRepository;
import com.slick.lead_revenue_automation_system.repositories.LeadRepository;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {
    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Test
    void returnsRecentActivitiesInChronologicalOrder() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        UUID leadId = UUID.fromString("62d248a9-a3e2-40b1-8f0a-0b30f8264603");
        Activity newest = activity("latest", Instant.parse("2026-10-13T12:00:00Z"));
        Activity older = activity("earlier", Instant.parse("2026-10-13T11:00:00Z"));
        when(activityRepository.findTop8ByOrganization_IdAndLead_IdOrderByOccurredAtDescIdDesc(
                organizationId, leadId)).thenReturn(List.of(newest, older));

        var activities = activityService.getRecentActivities(organizationId, leadId);

        assertEquals(List.of("earlier", "latest"), activities.stream().map(item -> item.content()).toList());
        verify(activityRepository).findTop8ByOrganization_IdAndLead_IdOrderByOccurredAtDescIdDesc(
                organizationId, leadId);
    }

    @Test
    void recordsActivityAgainstLeadFromTheSameOrganization() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        UUID leadId = UUID.fromString("62d248a9-a3e2-40b1-8f0a-0b30f8264603");
        Lead lead = mock(Lead.class);
        when(leadRepository.findByOrganization_IdAndId(organizationId, leadId)).thenReturn(Optional.of(lead));
        LeadConversationInfo leadInfo = new LeadConversationInfo(leadId, "visitor-42", "Alex", null,
                null, null, com.slick.lead_revenue_automation_system.entities.enums.LeadSource.WEBSITE_CHATBOX,
                com.slick.lead_revenue_automation_system.entities.enums.LeadStatus.ACTIVE);

        activityService.recordMessage(organizationId, leadInfo, ActivityType.WEB_CHAT,
                ActivityDirection.INBOUND, "Hello");

        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(activityCaptor.capture());
        assertEquals(lead, activityCaptor.getValue().getLead());
        assertEquals(ActivityDirection.INBOUND, activityCaptor.getValue().getDirection());
        assertEquals("Hello", activityCaptor.getValue().getContent());
    }

    private Activity activity(String content, Instant occurredAt) {
        Activity activity = mock(Activity.class);
        when(activity.getType()).thenReturn(ActivityType.WEB_CHAT);
        when(activity.getDirection()).thenReturn(ActivityDirection.INBOUND);
        when(activity.getContent()).thenReturn(content);
        when(activity.getOccurredAt()).thenReturn(occurredAt);
        return activity;
    }
}