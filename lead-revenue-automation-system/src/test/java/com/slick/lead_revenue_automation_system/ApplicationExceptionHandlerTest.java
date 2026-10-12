package com.slick.lead_revenue_automation_system;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.slick.lead_revenue_automation_system.dto.ErrorResponse;
import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApplicationExceptionHandlerTest {
	@Test
	void returnsClientMessageAndServiceWithoutExposingTrace() {
		ApplicationExceptionHandler handler = new ApplicationExceptionHandler();
		ServiceUnavailableException exception = new ServiceUnavailableException(
				ServiceUnavailableException.GEMINI_SERVICE,
				"Gemini is temporarily unavailable. Please try again later.",
				new IllegalStateException("internal provider detail"));

		ResponseEntity<Object> response = handler.handleServiceUnavailable(exception);

		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
		assertEquals(List.of("Gemini is temporarily unavailable. Please try again later."),
				((ErrorResponse) response.getBody()).errors());
		assertFalse(((ErrorResponse) response.getBody()).errors().get(0).contains("internal provider detail"));
	}

	@Test
	void returnsSafeErrorResponseForUnexpectedException() {
		ApplicationExceptionHandler handler = new ApplicationExceptionHandler();
		ResponseEntity<Object> response = handler.handleUnexpectedException(
				new IllegalStateException("internal detail"));

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals(List.of("An unexpected error occurred. Please try again later."),
				((ErrorResponse) response.getBody()).errors());
	}
}