package com.slick.lead_revenue_automation_system.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slick.lead_revenue_automation_system.dto.ContactFormMessageRequest;
import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.dto.WebChatMessageRequest;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;
import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;
import com.slick.lead_revenue_automation_system.entities.enums.LeadStatus;
import com.slick.lead_revenue_automation_system.services.context.ActivityConversationInfo;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;
import com.slick.lead_revenue_automation_system.services.context.ContextBuilder;
import com.slick.lead_revenue_automation_system.services.context.ConversationContext;
import com.slick.lead_revenue_automation_system.services.context.PromptBuilder;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private AiModelRouter aiModelRouter;

    @Mock
    private ContextBuilder contextBuilder;

    @Mock
    private PromptBuilder promptBuilder;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private LeadService leadService;

    @Mock
    private ActivityService activityService;

    @Mock
    private OrganizationBusinessInfo organization;

    @Mock
    private ConversationContext conversationContext;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
        void persistsWebChatTurnAndPassesPreviousHistoryAndCurrentMessageToAi() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        UUID leadId = UUID.fromString("62d248a9-a3e2-40b1-8f0a-0b30f8264603");
        LeadConversationInfo lead = new LeadConversationInfo(leadId, "visitor-42", "Web Chat Visitor", null,
            null, null, LeadSource.WEBSITE_CHATBOX, LeadStatus.ACTIVE);
        List<ActivityConversationInfo> previousActivities = List.of(
            new ActivityConversationInfo(ActivityType.WEB_CHAT, ActivityDirection.INBOUND,
                null, "Previous question", null));
        RoutedResponse expected = new RoutedResponse("Answer", "OpenAI", "gpt-4.1-nano");
        when(organizationService.getOrganizationBusinessInfo(organizationId)).thenReturn(organization);
        when(leadService.findOrCreateWebChatLead(organizationId, "visitor-42")).thenReturn(lead);
        when(activityService.getRecentActivities(organizationId, leadId)).thenReturn(previousActivities);
        when(contextBuilder.build(organization, lead, previousActivities, null)).thenReturn(conversationContext);
        when(promptBuilder.buildSystemPrompt(conversationContext)).thenReturn("Business system prompt");
        when(aiModelRouter.generate("Business system prompt", "Customer question")).thenReturn(expected);

        RoutedResponse actual = messageService.handleWebChatMessage(
            new WebChatMessageRequest(organizationId, "visitor-42", "Customer question"));

        assertSame(expected, actual);
        InOrder order = inOrder(organizationService, leadService, activityService,
            contextBuilder, promptBuilder, aiModelRouter);
        order.verify(organizationService).getOrganizationBusinessInfo(organizationId);
        order.verify(leadService).findOrCreateWebChatLead(organizationId, "visitor-42");
        order.verify(activityService).getRecentActivities(organizationId, leadId);
        order.verify(activityService).recordMessage(organizationId, lead, ActivityType.WEB_CHAT,
            ActivityDirection.INBOUND, "Customer question");
        order.verify(contextBuilder).build(organization, lead, previousActivities, null);
        order.verify(promptBuilder).buildSystemPrompt(conversationContext);
        order.verify(aiModelRouter).generate("Business system prompt", "Customer question");
        order.verify(activityService).recordMessage(organizationId, lead, ActivityType.WEB_CHAT,
            ActivityDirection.OUTBOUND, "Answer");
        }

        @Test
        void contactFormCreatesLeadAndUsesEmailActivity() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        UUID leadId = UUID.fromString("62d248a9-a3e2-40b1-8f0a-0b30f8264603");
        LeadConversationInfo lead = new LeadConversationInfo(leadId, null, "Alex", "Rivera",
            "alex@example.test", null, LeadSource.WEBSITE_CONTACTFORM, LeadStatus.ACTIVE);
        when(organizationService.getOrganizationBusinessInfo(organizationId)).thenReturn(organization);
        when(leadService.createContactFormLead(organizationId, "Alex Rivera", "alex@example.test"))
            .thenReturn(lead);
        when(activityService.getRecentActivities(organizationId, leadId)).thenReturn(List.of());
        when(contextBuilder.build(organization, lead, List.of(), null)).thenReturn(conversationContext);
        when(promptBuilder.buildSystemPrompt(conversationContext)).thenReturn("System prompt");
        when(aiModelRouter.generate("System prompt", "Need a quote"))
            .thenReturn(new RoutedResponse("Thanks for reaching out.", "Gemini", "gemini-2.5-flash"));

        messageService.handleContactFormMessage(new ContactFormMessageRequest(
            organizationId, "Alex Rivera", "alex@example.test", "Need a quote"));

        verify(activityService).recordMessage(organizationId, lead, ActivityType.EMAIL,
            ActivityDirection.INBOUND, "Need a quote");
        verify(activityService).recordMessage(organizationId, lead, ActivityType.EMAIL,
            ActivityDirection.OUTBOUND, "Thanks for reaching out.");
    }

    @Test
    void preservesMessageOnlyFlowForExistingWebhookCaller() {
        RoutedResponse expected = new RoutedResponse("Answer", "Gemini", "gemini-2.5-flash");
        when(aiModelRouter.generate("Incoming webhook message")).thenReturn(expected);

        assertSame(expected, messageService.sendMessage("Incoming webhook message"));
    }
}