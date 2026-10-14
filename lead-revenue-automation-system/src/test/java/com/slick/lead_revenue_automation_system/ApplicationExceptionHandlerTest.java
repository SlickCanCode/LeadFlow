package com.slick.lead_revenue_automation_system;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.slick.lead_revenue_automation_system.dto.ErrorResponse;
import com.slick.lead_revenue_automation_system.exception.OrganizationConflictException;
import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApplicationExceptionHandlerTest {
	@Test
	void returnsConflictForOrganizationSlugCollision() {
		ApplicationExceptionHandler handler = new ApplicationExceptionHandler();
		OrganizationConflictException exception = new OrganizationConflictException(
				"The organization could not be created because its URL conflicts with another organization.",
				new IllegalStateException("database detail"));

		ResponseEntity<Object> response = handler.handleOrganizationConflict(exception);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals(List.of(exception.getMessage()), ((ErrorResponse) response.getBody()).errors());
	}

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