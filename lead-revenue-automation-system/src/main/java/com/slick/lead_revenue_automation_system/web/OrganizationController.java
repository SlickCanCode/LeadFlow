package com.slick.lead_revenue_automation_system.web;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.dto.CreateOrganizationdto;
import com.slick.lead_revenue_automation_system.dto.CreateOrganizationSettingsRequest;
import com.slick.lead_revenue_automation_system.dto.CreateLeadRequest;
import com.slick.lead_revenue_automation_system.dto.LeadResponse;
import com.slick.lead_revenue_automation_system.dto.OrganizationResponse;
import com.slick.lead_revenue_automation_system.dto.OrganizationSettingsResponse;
import com.slick.lead_revenue_automation_system.services.LeadService;
import com.slick.lead_revenue_automation_system.services.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/organization")
public class OrganizationController {
	private final OrganizationService organizationService;
	private final LeadService leadService;

	public OrganizationController(OrganizationService organizationService, LeadService leadService) {
		this.organizationService = organizationService;
		this.leadService = leadService;
	}

	@PostMapping
	public ResponseEntity<OrganizationResponse> createOrganization(
			@Valid @RequestBody CreateOrganizationdto request) {
		OrganizationResponse response = organizationService.createOrganization(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{organizationId}/settings")
	public ResponseEntity<OrganizationSettingsResponse> createOrganizationSettings(
			@PathVariable UUID organizationId,
			@Valid @RequestBody CreateOrganizationSettingsRequest request) {
		OrganizationSettingsResponse response = organizationService.createOrganizationSettings(organizationId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/lead")
	public ResponseEntity<LeadResponse> createManualLead(@Valid @RequestBody CreateLeadRequest request) {
		LeadResponse response = leadService.createManualLead(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
