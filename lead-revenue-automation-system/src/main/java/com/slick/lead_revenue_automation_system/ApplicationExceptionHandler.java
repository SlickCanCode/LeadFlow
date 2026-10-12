package com.slick.lead_revenue_automation_system;

import java.util.Arrays;

import com.slick.lead_revenue_automation_system.dto.ErrorResponse;
import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

	@ExceptionHandler(ServiceUnavailableException.class)
	public ResponseEntity<Object> handleServiceUnavailable(RuntimeException exception) {
		if (exception instanceof ServiceUnavailableException serviceException) {
			logger.error("Service {} is unavailable", serviceException.getServiceName(), exception);
		} else {
			logger.error("A service is unavailable", exception);
		}

		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(Exception exception) {
		logger.error("Unexpected application error", exception);

		ErrorResponse error = new ErrorResponse(Arrays.asList("An unexpected error occurred. Please try again later."));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
