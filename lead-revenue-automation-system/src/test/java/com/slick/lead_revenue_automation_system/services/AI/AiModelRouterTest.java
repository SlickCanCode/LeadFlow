package com.slick.lead_revenue_automation_system.services.AI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;

class AiModelRouterTest {
	@Test
	void skipsModelsBelowVersionFloorsAndFallsBackInOrder() {
		List<String> openAiAttempts = new ArrayList<>();
		List<String> geminiAttempts = new ArrayList<>();
		AiService openAi = (systemPrompt, userMessage, model) -> {
			openAiAttempts.add(model);
			throw new IllegalStateException("unavailable");
		};
		AiService gemini = (systemPrompt, userMessage, model) -> {
			geminiAttempts.add(model);
			assertEquals("Business prompt", systemPrompt);
			assertEquals("Hello", userMessage);
			return "Reply";
		};
		AiModelRouter router = new AiModelRouter(
				openAi,
				gemini,
				List.of("gpt-3.5-turbo", "gpt-4.1-nano", "gpt-4o-mini"),
				List.of("gemini-2.0-flash", "gemini-2.5-flash", "gemini-2.5-flash-lite"));

		AiModelRouter.RoutedResponse response = router.generate("Business prompt", "Hello");

		assertEquals(List.of("gpt-4.1-nano", "gpt-4o-mini"), openAiAttempts);
		assertEquals(List.of("gemini-2.5-flash"), geminiAttempts);
		assertEquals("Reply", response.response());
		assertEquals("Gemini", response.provider());
		assertEquals("gemini-2.5-flash", response.model());
	}

	@Test
	void returnsFromOpenAiWithoutCallingGeminiWhenAvailable() {
		List<String> geminiAttempts = new ArrayList<>();
		AiService openAi = (systemPrompt, userMessage, model) -> {
			assertEquals("Business prompt", systemPrompt);
			assertEquals("Hello", userMessage);
			return "OpenAI reply";
		};
		AiService gemini = (systemPrompt, userMessage, model) -> {
			geminiAttempts.add(model);
			return "Gemini reply";
		};
		AiModelRouter router = new AiModelRouter(openAi, gemini,
				List.of("gpt-4.1-nano"), List.of("gemini-2.5-flash"));

		AiModelRouter.RoutedResponse response = router.generate("Business prompt", "Hello");

		assertEquals("OpenAI", response.provider());
		assertEquals("OpenAI reply", response.response());
		assertEquals(List.of(), geminiAttempts);
	}

	@Test
	void preservesGeminiServiceErrorAfterAllModelsFail() {
		ServiceUnavailableException geminiFailure = new ServiceUnavailableException(
				ServiceUnavailableException.GEMINI_SERVICE,
				"Gemini is temporarily unavailable. Please try again later.",
				new IllegalStateException("provider failure"));
		AiModelRouter router = new AiModelRouter(
				(systemPrompt, userMessage, model) -> "unused",
				(systemPrompt, userMessage, model) -> { throw geminiFailure; },
				List.of(),
				List.of("gemini-2.5-flash"));

		ServiceUnavailableException thrown = assertThrows(ServiceUnavailableException.class,
				() -> router.generate("Business prompt", "Hello"));

		assertSame(geminiFailure, thrown);
		assertEquals(ServiceUnavailableException.GEMINI_SERVICE, thrown.getServiceName());
	}

	@Test
	void identifiesRouterWhenNoModelsAreConfigured() {
		AiModelRouter router = new AiModelRouter(
				(systemPrompt, userMessage, model) -> "unused",
				(systemPrompt, userMessage, model) -> "unused",
				List.of(),
				List.of());

		ServiceUnavailableException thrown = assertThrows(ServiceUnavailableException.class,
				() -> router.generate("Business prompt", "Hello"));

		assertEquals(ServiceUnavailableException.AI_MODEL_ROUTER, thrown.getServiceName());
		assertEquals("We couldn't find an available AI model to process your request. Please try again later.",
				thrown.getMessage());
	}
}