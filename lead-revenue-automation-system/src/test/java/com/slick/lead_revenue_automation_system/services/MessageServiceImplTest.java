package com.slick.lead_revenue_automation_system.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;
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
    private ConversationContext conversationContext;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void buildsOrganizationContextAndPassesSystemAndUserMessagesToRouter() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        RoutedResponse expected = new RoutedResponse("Answer", "OpenAI", "gpt-4.1-nano");
        when(contextBuilder.build(organizationId)).thenReturn(conversationContext);
        when(promptBuilder.buildSystemPrompt(conversationContext)).thenReturn("Business system prompt");
        when(aiModelRouter.generate("Business system prompt", "Customer question")).thenReturn(expected);

        RoutedResponse actual = messageService.sendMessage(organizationId, "Customer question");

        assertSame(expected, actual);
        verify(contextBuilder).build(organizationId);
        verify(promptBuilder).buildSystemPrompt(conversationContext);
        verify(aiModelRouter).generate("Business system prompt", "Customer question");
    }

    @Test
    void preservesMessageOnlyFlowForExistingWebhookCaller() {
        RoutedResponse expected = new RoutedResponse("Answer", "Gemini", "gemini-2.5-flash");
        when(aiModelRouter.generate("Incoming webhook message")).thenReturn(expected);

        assertSame(expected, messageService.sendMessage("Incoming webhook message"));
    }
}