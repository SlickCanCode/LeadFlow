package com.slick.lead_revenue_automation_system.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;
import com.slick.lead_revenue_automation_system.dto.ContactFormMessageRequest;
import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.dto.WebChatMessageRequest;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityDirection;
import com.slick.lead_revenue_automation_system.entities.enums.ActivityType;
import com.slick.lead_revenue_automation_system.services.context.ActivityConversationInfo;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;
import com.slick.lead_revenue_automation_system.services.context.ContextBuilder;
import com.slick.lead_revenue_automation_system.services.context.ConversationContext;
import com.slick.lead_revenue_automation_system.services.context.PromptBuilder;

@Service
public class MessageServiceImpl implements MessageService {
	private final AiModelRouter aiModelRouter;
	private final ContextBuilder contextBuilder;
	private final PromptBuilder promptBuilder;
	private final OrganizationService organizationService;
	private final LeadService leadService;
	private final ActivityService activityService;

	public MessageServiceImpl(AiModelRouter aiModelRouter, ContextBuilder contextBuilder, PromptBuilder promptBuilder,
			OrganizationService organizationService, LeadService leadService, ActivityService activityService) {
		this.aiModelRouter = aiModelRouter;
		this.contextBuilder = contextBuilder;
		this.promptBuilder = promptBuilder;
		this.organizationService = organizationService;
		this.leadService = leadService;
		this.activityService = activityService;
	}

	@Override
	public RoutedResponse sendMessage(String message) {
		return aiModelRouter.generate(message);
	}

	@Override
	public RoutedResponse sendMessage(UUID organizationId, String message) {
		OrganizationBusinessInfo organization = organizationService.getOrganizationBusinessInfo(organizationId);
		ConversationContext context = contextBuilder.build(organization, null, List.of(), null);
		String systemPrompt = promptBuilder.buildSystemPrompt(context);
		return aiModelRouter.generate(systemPrompt, message);
	}

	@Override
	public RoutedResponse handleWebChatMessage(WebChatMessageRequest request) {
		OrganizationBusinessInfo organization = organizationService.getOrganizationBusinessInfo(request.organizationId());
		LeadConversationInfo lead = leadService.findOrCreateWebChatLead(request.organizationId(), request.externalId());
		return handleInboundMessage(request.organizationId(), organization, lead, ActivityType.WEB_CHAT,
				request.message());
	}

	@Override
	public RoutedResponse handleContactFormMessage(ContactFormMessageRequest request) {
		OrganizationBusinessInfo organization = organizationService.getOrganizationBusinessInfo(request.organizationId());
		LeadConversationInfo lead = leadService.createContactFormLead(
				request.organizationId(), request.name(), request.email());
		return handleInboundMessage(request.organizationId(), organization, lead, ActivityType.EMAIL,
				request.message());
	}

	private RoutedResponse handleInboundMessage(UUID organizationId, OrganizationBusinessInfo organization,
			LeadConversationInfo lead, ActivityType activityType, String message) {
		List<ActivityConversationInfo> recentActivities = activityService.getRecentActivities(organizationId, lead.id());
		activityService.recordMessage(organizationId, lead, activityType, ActivityDirection.INBOUND, message);

		ConversationContext context = contextBuilder.build(organization, lead, recentActivities, null);
		String systemPrompt = promptBuilder.buildSystemPrompt(context);
		RoutedResponse response = aiModelRouter.generate(systemPrompt, message);

		activityService.recordMessage(organizationId, lead, activityType, ActivityDirection.OUTBOUND,
				response.response());
		return response;
	}
}
