package com.slick.lead_revenue_automation_system.exception;

import java.util.UUID;

public class OrganizationSettingsNotFoundException extends RuntimeException {
    public OrganizationSettingsNotFoundException(UUID organizationId) {
        super("Settings for organization " + organizationId + " were not found.");
    }
}
