package com.slick.lead_revenue_automation_system.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;
import com.slick.lead_revenue_automation_system.services.context.ContextBuilder;
import com.slick.lead_revenue_automation_system.services.context.ConversationContext;
import com.slick.lead_revenue_automation_system.services.context.PromptBuilder;

@Service
public class MessageServiceImpl implements MessageService {
	private final AiModelRouter aiModelRouter;
	private final ContextBuilder contextBuilder;
	private final PromptBuilder promptBuilder;

	public MessageServiceImpl(AiModelRouter aiModelRouter, ContextBuilder contextBuilder, PromptBuilder promptBuilder) {
		this.aiModelRouter = aiModelRouter;
		this.contextBuilder = contextBuilder;
		this.promptBuilder = promptBuilder;
	}

	@Override
	public RoutedResponse sendMessage(String message) {
		return aiModelRouter.generate(message);
	}

	@Override
	public RoutedResponse sendMessage(UUID organizationId, String message) {
		ConversationContext context = contextBuilder.build(organizationId);
		String systemPrompt = promptBuilder.buildSystemPrompt(context);
		return aiModelRouter.generate(systemPrompt, message);
	}
}
