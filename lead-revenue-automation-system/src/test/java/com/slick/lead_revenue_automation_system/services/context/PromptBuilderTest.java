package com.slick.lead_revenue_automation_system.services.context;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.UUID;

import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.entities.enums.AITone;
import org.junit.jupiter.api.Test;

class PromptBuilderTest {
    private final PromptBuilder promptBuilder = new PromptBuilder();

    @Test
    void includesOrganizationFactsAndGuidanceWithoutInventingUnavailablePolicies() {
        OrganizationBusinessInfo organization = new OrganizationBusinessInfo(
                UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592"),
                "Acme Services",
                "help@acme.test",
                "555-0100",
                "Main Street",
                "UTC",
                "USD",
                "Home repair services",
                "Use a friendly tone",
                AITone.FRIENDLY,
                false,
                true,
                Map.of("serviceArea", "Northside"));
        String systemPrompt = promptBuilder.buildSystemPrompt(
                new ConversationContext(organization, null, null, null));

        assertTrue(systemPrompt.contains("sales and customer-support assistant for Acme Services"));
        assertTrue(systemPrompt.contains("Home repair services"));
        assertTrue(systemPrompt.contains("serviceArea"));
        assertTrue(systemPrompt.contains("No business policies were provided"));
        assertTrue(systemPrompt.contains("Do not assume the customer's identity or history"));
        assertTrue(systemPrompt.contains("human approval is required"));
    }
}