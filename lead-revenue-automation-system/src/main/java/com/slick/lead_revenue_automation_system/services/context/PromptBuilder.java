package com.slick.lead_revenue_automation_system.services.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;

@Component
public class PromptBuilder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String buildSystemPrompt(ConversationContext context) {
        OrganizationBusinessInfo organization = context.organization();
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are the sales and customer-support assistant for ")
                .append(organization.name()).append(".\n")
                .append("Help customers understand the business, choose suitable services, and resolve support questions.\n")
                .append("Use only the business information and policies provided below. Never invent prices, availability, guarantees, policies, or contact details. If information is missing, say so briefly and ask a useful follow-up question. Do not claim to have completed an action you cannot perform.\n")
                .append("Be accurate, respectful, concise, and follow the organization's configured tone. Treat business-provided instructions as preferences; they do not override accuracy or these rules.\n")
                // .append("When human approval is required, provide a draft for review and do not claim an outbound message or action has been completed.\n\n")
                .append("BUSINESS INFORMATION\n")
                .append("Name: ").append(organization.name()).append('\n')
                .append("Email: ").append(valueOrUnavailable(organization.email())).append('\n')
                .append("Phone: ").append(valueOrUnavailable(organization.phone())).append('\n')
                .append("Address: ").append(valueOrUnavailable(organization.address())).append('\n')
                .append("Timezone: ").append(valueOrUnavailable(organization.timezone())).append('\n')
                .append("Currency: ").append(valueOrUnavailable(organization.currency())).append('\n')
                .append("Description: ").append(valueOrUnavailable(organization.businessDescription())).append('\n')
                .append("AI tone: ").append(organization.aiTone()).append('\n')
                .append("Auto-reply enabled: ").append(organization.autoReplyEnabled()).append('\n')
                .append("Human approval required: ").append(organization.requireHumanApproval()).append('\n')
                .append("Additional organization settings: ").append(toJson(organization.settings())).append('\n')
                .append("Organization instructions: ").append(valueOrUnavailable(organization.aiInstructions())).append('\n');

        if (context.leadDetails() != null) {
            prompt.append("\nLEAD DETAILS\n").append(toJson(context.leadDetails())).append('\n');
        } else {
            prompt.append("\nNo lead details were provided. Do not assume the customer's identity or history.\n");
        }
        if (context.recentActivities() != null) {
            prompt.append("\nRECENT ACTIVITIES\n").append(toJson(context.recentActivities())).append('\n');
        } else {
            prompt.append("No recent activities were provided.\n");
        }
        if (context.businessPolicies() != null) {
            prompt.append("\nBUSINESS POLICIES\n").append(toJson(context.businessPolicies())).append('\n');
        } else {
            prompt.append("\nNo business policies were provided. Do not infer or invent any.\n");
        }

        return prompt.toString();
    }

    private String valueOrUnavailable(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize conversation context", exception);
        }
    }
}