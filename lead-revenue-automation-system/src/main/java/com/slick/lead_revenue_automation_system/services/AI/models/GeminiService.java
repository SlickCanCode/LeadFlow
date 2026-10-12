package com.slick.lead_revenue_automation_system.services.AI.models;

import com.slick.lead_revenue_automation_system.services.AI.AiService;
import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;

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
	public String generate(String message, String model) {
		try {
			ChatResponse response = chatModel.call(
				new Prompt(
					message,
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

}
