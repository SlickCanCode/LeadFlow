package com.slick.lead_revenue_automation_system.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class OpenAiConfiguration {

	@Bean
	@Lazy
	OpenAiChatModel openAiChatModel(
			@Value("${spring.ai.openai.api-key}") String apiKey,
			@Value("${spring.ai.openai.base-url}") String baseUrl) {
		return OpenAiChatModel.builder()
				.options(OpenAiChatOptions.builder().apiKey(apiKey).baseUrl(baseUrl).build())
				.build();
	}
}