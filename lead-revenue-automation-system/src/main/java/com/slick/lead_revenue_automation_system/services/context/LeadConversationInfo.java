package com.slick.lead_revenue_automation_system.services.context;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;
import com.slick.lead_revenue_automation_system.entities.enums.LeadStatus;

public record LeadConversationInfo(
        UUID id,
        String externalId,
        String firstName,
        String lastName,
        String email,
        String phone,
        LeadSource source,
        LeadStatus status) {
    public String displayName() {
        return lastName == null || lastName.isBlank() ? firstName : firstName + " " + lastName;
    }
}