package com.slick.lead_revenue_automation_system.exception;

import java.util.UUID;

public class OrganizationSettingsConflictException extends RuntimeException {
    public OrganizationSettingsConflictException(UUID organizationId) {
        super("Settings already exist for organization " + organizationId + ".");
    }

    public OrganizationSettingsConflictException(UUID organizationId, Throwable cause) {
        super("Settings already exist for organization " + organizationId + ".", cause);
    }
}