package com.slick.lead_revenue_automation_system.services;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;

public interface MessageService {
	RoutedResponse sendMessage(String message);

	RoutedResponse sendMessage(UUID organizationId, String message);
}
