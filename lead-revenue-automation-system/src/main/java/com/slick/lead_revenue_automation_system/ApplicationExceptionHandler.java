package com.slick.lead_revenue_automation_system;

import java.util.Arrays;

import com.slick.lead_revenue_automation_system.dto.ErrorResponse;
import com.slick.lead_revenue_automation_system.exception.OrganizationConflictException;
import com.slick.lead_revenue_automation_system.exception.LeadConflictException;
import com.slick.lead_revenue_automation_system.exception.LeadNotFoundException;
import com.slick.lead_revenue_automation_system.exception.OrganizationNotFoundException;
import com.slick.lead_revenue_automation_system.exception.OrganizationSettingsConflictException;
import com.slick.lead_revenue_automation_system.exception.OrganizationBusinessSettingsRequiredException;
import com.slick.lead_revenue_automation_system.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

	@ExceptionHandler(LeadNotFoundException.class)
	public ResponseEntity<Object> handleLeadNotFound(LeadNotFoundException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(LeadConflictException.class)
	public ResponseEntity<Object> handleLeadConflict(LeadConflictException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(OrganizationBusinessSettingsRequiredException.class)
	public ResponseEntity<Object> handleMissingBusinessSettings(
			OrganizationBusinessSettingsRequiredException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(OrganizationNotFoundException.class)
	public ResponseEntity<Object> handleOrganizationNotFound(OrganizationNotFoundException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OrganizationSettingsConflictException.class)
	public ResponseEntity<Object> handleOrganizationSettingsConflict(OrganizationSettingsConflictException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(OrganizationConflictException.class)
	public ResponseEntity<Object> handleOrganizationConflict(OrganizationConflictException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException exception) {
		ErrorResponse error = new ErrorResponse(exception.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getDefaultMessage())
				.toList());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleMalformedRequest(HttpMessageNotReadableException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList("Request body contains invalid or malformed values."));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleInvalidPathParameter(MethodArgumentTypeMismatchException exception) {
		ErrorResponse error = new ErrorResponse(Arrays.asList("A path parameter has an invalid format."));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

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
