package com.slick.lead_revenue_automation_system.exception;

import java.util.UUID;

public class OrganizationNotFoundException extends RuntimeException {
    public OrganizationNotFoundException(UUID organizationId) {
        super("Organization " + organizationId + " was not found.");
    }
}