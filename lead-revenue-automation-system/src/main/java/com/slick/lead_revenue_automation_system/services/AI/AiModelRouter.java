package com.slick.lead_revenue_automation_system.services.AI;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AiModelRouter {
	private static final Pattern OPENAI_VERSION = Pattern.compile("^gpt-(\\d+)(?:\\.(\\d+))?.*$", Pattern.CASE_INSENSITIVE);
	private static final Pattern GEMINI_FLASH_VERSION = Pattern.compile("^gemini-(\\d+)(?:\\.(\\d+))?-flash(?:[-.]|$).*$", Pattern.CASE_INSENSITIVE);

	private final AiService openAiService;
	private final AiService geminiService;
	private final List<String> openAiModels;
	private final List<String> geminiModels;

	public AiModelRouter(
			@Qualifier("openAiService") AiService openAiService,
			@Qualifier("geminiService") AiService geminiService,
			@Value("${ai.openai.models}") List<String> openAiModels,
			@Value("${ai.gemini.models}") List<String> geminiModels) {
		this.openAiService = openAiService;
		this.geminiService = geminiService;
		this.openAiModels = openAiModels.stream().map(String::trim).filter(AiModelRouter::isEligibleOpenAiModel).toList();
		this.geminiModels = geminiModels.stream().map(String::trim).filter(AiModelRouter::isEligibleGeminiModel).toList();
	}

	public RoutedResponse generate(String message) {
		RuntimeException lastFailure = null;

		// for (String model : openAiModels) {
		// 	try {
		// 		return new RoutedResponse(openAiService.generate(message, model), "OpenAI", model);
		// 	} catch (RuntimeException exception) {
		// 		lastFailure = exception;
		// 	}
		// }

		for (String model : geminiModels) {
			try {
				return new RoutedResponse(geminiService.generate(message, model), "Gemini", model);
			} catch (RuntimeException exception) {
				lastFailure = exception;
			}
		}

		if (lastFailure instanceof ServiceUnavailableException serviceUnavailableException) {
			throw serviceUnavailableException;
		}
		throw new ServiceUnavailableException(
				ServiceUnavailableException.AI_MODEL_ROUTER,
				"We couldn't find an available AI model to process your request. Please try again later.",
				lastFailure);
	}

	private static boolean isEligibleOpenAiModel(String model) {
		Matcher matcher = OPENAI_VERSION.matcher(model);
		return !model.isBlank() && matcher.matches() && Integer.parseInt(matcher.group(1)) >= 4;
	}

	private static boolean isEligibleGeminiModel(String model) {
		Matcher matcher = GEMINI_FLASH_VERSION.matcher(model);
		if (!matcher.matches()) {
			return false;
		}

		int majorVersion = Integer.parseInt(matcher.group(1));
		int minorVersion = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
		return majorVersion > 2 || (majorVersion == 2 && minorVersion >= 1);
	}

	public record RoutedResponse(String response, String provider, String model) {
	}
}
