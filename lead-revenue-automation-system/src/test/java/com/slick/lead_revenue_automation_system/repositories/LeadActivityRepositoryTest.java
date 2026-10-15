package com.slick.lead_revenue_automation_system.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.slick.lead_revenue_automation_system.entities.Activity;
import com.slick.lead_revenue_automation_system.entities.Lead;
import com.slick.lead_revenue_automation_system.entities.Organization;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;
import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;

@SpringBootTest
@Transactional
class LeadActivityRepositoryTest {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Test
    void scopesExternalIdUniquenessAndRecentActivityWindowToOrganizationAndLead() {
        Organization firstOrganization = saveOrganization("First Plumbing", "first-plumbing");
        Organization secondOrganization = saveOrganization("Second Plumbing", "second-plumbing");
        Lead firstLead = saveLead(firstOrganization, "visitor-123");
        Lead secondOrganizationLead = saveLead(secondOrganization, "visitor-123");

        List<Activity> activities = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            activities.add(new Activity(firstLead, ActivityType.WEB_CHAT, ActivityDirection.INBOUND,
                    null, "message-" + index, Instant.parse("2026-10-13T10:00:00Z").plusSeconds(index)));
        }
        activityRepository.saveAllAndFlush(activities);

        List<Activity> recentActivities = activityRepository
                .findTop8ByOrganization_IdAndLead_IdOrderByOccurredAtDescIdDesc(
                        firstOrganization.getId(), firstLead.getId());

        assertEquals(8, recentActivities.size());
        assertEquals("message-9", recentActivities.get(0).getContent());
        assertEquals("message-2", recentActivities.get(7).getContent());
        assertEquals(List.of(), activityRepository
                .findTop8ByOrganization_IdAndLead_IdOrderByOccurredAtDescIdDesc(
                        secondOrganization.getId(), secondOrganizationLead.getId()));

        assertThrows(DataIntegrityViolationException.class,
                () -> leadRepository.saveAndFlush(new Lead(firstOrganization, "visitor-123", "Duplicate",
                        null, null, null, LeadSource.WEBSITE_CHATBOX)));
    }

    private Organization saveOrganization(String name, String slug) {
        return organizationRepository.saveAndFlush(new Organization(name, slug, null, null, null,
                null, null, null));
    }

    private Lead saveLead(Organization organization, String externalId) {
        return leadRepository.saveAndFlush(new Lead(organization, externalId, "Web Chat Visitor", null,
                null, null, LeadSource.WEBSITE_CHATBOX));
    }
}