package com.slick.lead_revenue_automation_system.web;

import com.slick.lead_revenue_automation_system.dto.MessageRequest;
import com.slick.lead_revenue_automation_system.dto.MessageResponse;
import com.slick.lead_revenue_automation_system.services.MessageService;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/message")
public class MessageController {
	private final MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping("/send")
	public MessageResponse sendMessage(@Valid @RequestBody MessageRequest request) {
		RoutedResponse response = messageService.sendMessage(request.organizationId(), request.message());
		return new MessageResponse(response.response(), response.provider(), response.model());
        
	}
}
