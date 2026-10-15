package com.slick.lead_revenue_automation_system.dto;

import java.util.regex.Pattern;

final class LeadRequestValidation {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private LeadRequestValidation() {
    }

    static boolean nameFitsEntityFields(String name) {
        if (name == null) {
            return true;
        }
        String[] parts = WHITESPACE.split(name.trim(), 2);
        return parts.length > 0 && parts[0].length() <= 100
                && (parts.length == 1 || parts[1].length() <= 100);
    }
}