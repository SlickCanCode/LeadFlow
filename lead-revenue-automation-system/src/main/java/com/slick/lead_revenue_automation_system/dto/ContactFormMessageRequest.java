package com.slick.lead_revenue_automation_system.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactFormMessageRequest(
        @NotNull(message = "Organization ID is required") UUID organizationId,
        @NotBlank(message = "Lead name is required") @Size(max = 201, message = "Lead name is too long") String name,
        @NotBlank(message = "Lead email is required")
        @Email(message = "Lead email must be valid") @Size(max = 255, message = "Lead email is too long") String email,
        @NotBlank(message = "Message is required") String message) {
    public ContactFormMessageRequest {
        name = trim(name);
        email = trim(email);
        message = message == null ? null : message.trim();
    }

    @AssertTrue(message = "Lead name must fit the first and last name fields")
    public boolean isNameWithinFieldLimits() {
        return LeadRequestValidation.nameFitsEntityFields(name);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}