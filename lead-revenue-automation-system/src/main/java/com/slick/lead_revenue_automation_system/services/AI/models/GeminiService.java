package com.slick.lead_revenue_automation_system.services.AI.models;

import java.util.List;

import com.slick.lead_revenue_automation_system.services.AI.AiService;
import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component ("geminiService")
public class GeminiService implements AiService{

	private final ChatModel chatModel;

	public GeminiService(@Qualifier("googleGenAiChatModel") ChatModel chatModel) {
		this.chatModel = chatModel;
	}

	@Override
	public String generate(String systemPrompt, String userMessage, String model) {
		try {
			ChatResponse response = chatModel.call(
				new Prompt(
					createMessages(systemPrompt, userMessage),
					GoogleGenAiChatOptions.builder()
						.model(model)
						.temperature(0.1)
						.build()
				));
			String generatedText = response.getResults().get(0).getOutput().getText();
			if (generatedText == null || generatedText.isBlank()) {
				throw new IllegalStateException("Gemini returned no message content");
			}
			return generatedText;
		} catch (RuntimeException exception) {
			throw new ServiceUnavailableException(
					ServiceUnavailableException.GEMINI_SERVICE,
					"Gemini is temporarily unavailable. Please try again later.",
					exception);
		}
	}

	private List<Message> createMessages(String systemPrompt, String userMessage) {
		if (systemPrompt == null || systemPrompt.isBlank()) {
			return List.of(new UserMessage(userMessage));
		}
		return List.of(new SystemMessage(systemPrompt), new UserMessage(userMessage));
	}

}
