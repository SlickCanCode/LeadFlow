package com.slick.lead_revenue_automation_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.util.Locale;

public record CreateOrganizationdto(

    @NotBlank(message = "Organization name is required")
    @Size(max = 150, message = "Organization name cannot exceed 150 characters")
    String name,

    @Email(message = "Organization email must be valid")
    @Size(max = 255, message = "Organization email cannot exceed 255 characters")
    String email,

    @Size(max = 50, message = "Organization phone cannot exceed 50 characters")
    String phone,

    String address,

    @Size(max = 500, message = "Organization logo URL cannot exceed 500 characters")
    String logoUrl,

    @Size(max = 50, message = "Organization timezone cannot exceed 50 characters")
    String timezone,

    @Pattern(regexp = "[A-Za-z]{3}", message = "Organization currency must be a 3-letter code")
    String currency
) {
    public CreateOrganizationdto {
        name = trim(name);
        email = trimToNull(email);
        phone = trimToNull(phone);
        address = trimToNull(address);
        logoUrl = trimToNull(logoUrl);
        timezone = trimToNull(timezone);
        currency = trimToNull(currency);
        currency = currency == null ? null : currency.toUpperCase(Locale.ROOT);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToNull(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }
}
