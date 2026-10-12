package com.slick.lead_revenue_automation_system.services;

import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;

public interface MessageService {
	RoutedResponse sendMessage(String message);
}
