package com.slick.lead_revenue_automation_system.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slick.lead_revenue_automation_system.entities.Activity;
import com.slick.lead_revenue_automation_system.entities.Lead;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;
import com.slick.lead_revenue_automation_system.exception.LeadNotFoundException;
import com.slick.lead_revenue_automation_system.repositories.ActivityRepository;
import com.slick.lead_revenue_automation_system.repositories.LeadRepository;
import com.slick.lead_revenue_automation_system.services.context.ActivityConversationInfo;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final LeadRepository leadRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, LeadRepository leadRepository) {
        this.activityRepository = activityRepository;
        this.leadRepository = leadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityConversationInfo> getRecentActivities(UUID organizationId, UUID leadId) {
        List<Activity> newestFirst = activityRepository
                .findTop8ByOrganization_IdAndLead_IdOrderByOccurredAtDescIdDesc(organizationId, leadId);
        List<ActivityConversationInfo> chronological = new ArrayList<>(newestFirst.stream()
                .map(ActivityServiceImpl::toConversationInfo)
                .toList());
        Collections.reverse(chronological);
        return List.copyOf(chronological);
    }

    @Override
    @Transactional
    public void recordMessage(UUID organizationId, LeadConversationInfo leadInfo, ActivityType type,
            ActivityDirection direction, String content) {
        Lead lead = leadRepository.findByOrganization_IdAndId(organizationId, leadInfo.id())
                .orElseThrow(() -> new LeadNotFoundException(leadInfo.id(), organizationId));
        Activity activity = new Activity(lead, type, direction, null, content, Instant.now());
        activityRepository.save(activity);
    }

    private static ActivityConversationInfo toConversationInfo(Activity activity) {
        return new ActivityConversationInfo(activity.getType(), activity.getDirection(),
                activity.getSubject(), activity.getContent(), activity.getOccurredAt());
    }
}