package com.slick.lead_revenue_automation_system.dto;

import jakarta.validation.constraints.NotNull;

public record MessageRequest(@NotNull(message = "Message is required") String message) {
}