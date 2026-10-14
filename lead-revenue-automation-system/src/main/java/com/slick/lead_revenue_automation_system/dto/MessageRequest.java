package com.slick.lead_revenue_automation_system.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequest(
	@NotNull(message = "Organization ID is required") UUID organizationId,
	@NotBlank(message = "Message is required") String message) {
}