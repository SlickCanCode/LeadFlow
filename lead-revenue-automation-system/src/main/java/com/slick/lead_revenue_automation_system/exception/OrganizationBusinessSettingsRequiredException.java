package com.slick.lead_revenue_automation_system.exception;

import java.util.UUID;

public class OrganizationBusinessSettingsRequiredException extends RuntimeException {
    public OrganizationBusinessSettingsRequiredException(UUID organizationId) {
        super("Business settings must be configured before messaging for organization " + organizationId + ".");
    }
}