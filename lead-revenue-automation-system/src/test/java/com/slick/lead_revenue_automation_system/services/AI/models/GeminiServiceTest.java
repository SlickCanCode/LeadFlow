package com.slick.lead_revenue_automation_system.services.AI.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

class GeminiServiceTest {
	@Test
	void wrapsProviderFailureWithGeminiSourceAndOriginalCause() {
		ChatModel chatModel = mock(ChatModel.class);
		IllegalStateException providerFailure = new IllegalStateException("provider detail");
		when(chatModel.call(any(Prompt.class))).thenThrow(providerFailure);
		GeminiService geminiService = new GeminiService(chatModel);

		ServiceUnavailableException thrown = assertThrows(ServiceUnavailableException.class,
				() -> geminiService.generate("Business prompt", "Hello", "gemini-2.5-flash"));

		assertEquals(ServiceUnavailableException.GEMINI_SERVICE, thrown.getServiceName());
		assertEquals("Gemini is temporarily unavailable. Please try again later.", thrown.getMessage());
		assertSame(providerFailure, thrown.getCause());

		ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
		org.mockito.Mockito.verify(chatModel).call(promptCaptor.capture());
		List<Message> messages = promptCaptor.getValue().getInstructions();
		assertEquals(2, messages.size());
		assertEquals("Business prompt", messages.get(0).getText());
		assertEquals("Hello", messages.get(1).getText());
	}
}