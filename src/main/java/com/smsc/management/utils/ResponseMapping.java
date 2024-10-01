package com.smsc.management.utils;

public class ResponseMapping {
	private static final String ERROR = "error";

	private ResponseMapping() {
		throw new IllegalStateException("Utility class");
	}

	public static ApiResponse errorMessage(String message) {
		return new ApiResponse(400, ERROR, message, null);
	}

	public static ApiResponse successMessage(String message, Object data) {
		return new ApiResponse(200, "success", message, data);
	}

	public static ApiResponse exceptionMessage(String message, Exception e) {
		String cause = e.getMessage();
		return new ApiResponse(500, ERROR, message + " (" + cause + ")", null);
	}
	
	public static ApiResponse errorMessageNoFound(String message) {
		return new ApiResponse(404, ERROR, message, null);
	}
}
