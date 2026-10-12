package com.slick.lead_revenue_automation_system.services.AI.models;

import com.slick.lead_revenue_automation_system.services.AI.AiService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("openAiService")
public class GptService implements AiService{
	private final ObjectProvider<OpenAiChatModel> chatModelProvider;
	private final String apiKey;

	public GptService(
			@Qualifier("openAiChatModel") ObjectProvider<OpenAiChatModel> chatModelProvider,
			@Value("${spring.ai.openai.api-key}") String apiKey) {
		this.chatModelProvider = chatModelProvider;
		this.apiKey = apiKey;
	}

	@Override
	public String generate(String message, String model) {
		if (apiKey.isBlank()) {
			throw new IllegalStateException("OPENAI_API_KEY is not configured");
		}

		ChatResponse response = chatModelProvider.getObject().call(
				new Prompt(message, OpenAiChatOptions.builder().model(model).build()));

		if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
			throw new IllegalStateException("OpenAI returned no message content");
		}
		String content = response.getResult().getOutput().getText();
		if (content == null || content.isBlank()) {
			throw new IllegalStateException("OpenAI returned no message content");
		}
		return content;
	}
}
