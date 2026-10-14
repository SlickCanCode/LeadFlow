package com.slick.lead_revenue_automation_system.services;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.dto.CreateOrganizationdto;
import com.slick.lead_revenue_automation_system.dto.CreateOrganizationSettingsRequest;
import com.slick.lead_revenue_automation_system.dto.OrganizationResponse;
import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.dto.OrganizationSettingsResponse;

public interface OrganizationService {
    OrganizationResponse createOrganization(CreateOrganizationdto request);

    OrganizationSettingsResponse createOrganizationSettings(
            UUID organizationId, CreateOrganizationSettingsRequest request);

    OrganizationBusinessInfo getOrganizationBusinessInfo(UUID organizationId);
}