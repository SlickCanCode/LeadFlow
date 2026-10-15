package com.slick.lead_revenue_automation_system.exception;

import java.util.UUID;

public class LeadNotFoundException extends RuntimeException {
    public LeadNotFoundException(UUID leadId, UUID organizationId) {
        super("Lead " + leadId + " was not found for organization " + organizationId + ".");
    }
}