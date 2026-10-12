package com.slick.lead_revenue_automation_system.exception;

public class ServiceUnavailableException extends RuntimeException {
	public static final String GEMINI_SERVICE = "Gemini";
	public static final String AI_MODEL_ROUTER = "AI model router";

	private final String serviceName;

	public ServiceUnavailableException(String serviceName, String clientMessage, Throwable cause) {
		super(clientMessage, cause);
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}
}