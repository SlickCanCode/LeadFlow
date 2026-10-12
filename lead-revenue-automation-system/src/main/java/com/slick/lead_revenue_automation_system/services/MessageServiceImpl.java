package com.slick.lead_revenue_automation_system.services;

import org.springframework.stereotype.Service;

import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;

@Service 
public class MessageServiceImpl implements MessageService {
	private final AiModelRouter aiModelRouter;

	public MessageServiceImpl(AiModelRouter aiModelRouter) {
		this.aiModelRouter = aiModelRouter;
	}

	@Override
	public RoutedResponse sendMessage(String message) {
		return aiModelRouter.generate(message);
	}
}
