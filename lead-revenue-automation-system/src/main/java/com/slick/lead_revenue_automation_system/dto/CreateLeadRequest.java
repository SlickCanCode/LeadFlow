package com.slick.lead_revenue_automation_system.dto;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLeadRequest(
    @NotNull(message = "Organization ID is required") UUID organizationId,
        @NotBlank(message = "Lead name is required") @Size(max = 201, message = "Lead name is too long") String name,
        @Email(message = "Lead email must be valid") @Size(max = 255, message = "Lead email is too long") String email,
        @Size(max = 50, message = "Lead phone cannot exceed 50 characters") String phone,
        @NotNull(message = "Lead source is required") LeadSource source) {
    public CreateLeadRequest {
        name = trimToNull(name);
        email = trimToNull(email);
        phone = trimToNull(phone);
    }

    @AssertTrue(message = "At least one of lead email or phone is required")
    public boolean isContactInformationProvided() {
        return email != null || phone != null;
    }

    @AssertTrue(message = "Lead name must fit the first and last name fields")
    public boolean isNameWithinFieldLimits() {
        return LeadRequestValidation.nameFitsEntityFields(name);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}