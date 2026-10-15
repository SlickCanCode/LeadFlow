package com.slick.lead_revenue_automation_system.services;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.dto.CreateLeadRequest;
import com.slick.lead_revenue_automation_system.dto.LeadResponse;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;

public interface LeadService {
    LeadResponse createManualLead(CreateLeadRequest request);

    LeadConversationInfo findOrCreateWebChatLead(UUID organizationId, String externalId);

    LeadConversationInfo createContactFormLead(UUID organizationId, String name, String email);
}