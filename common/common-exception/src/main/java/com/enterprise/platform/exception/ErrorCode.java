package com.enterprise.platform.exception;

public enum ErrorCode {

    GENERIC_ERROR("GENERIC_ERROR", "An unexpected error occurred"),
    NOT_FOUND("NOT_FOUND", "Resource not found"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "Service is unavailable"),
    GATEWAY_ERROR("GATEWAY_ERROR", "Gateway error occurred"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    PARKING_SLOT_NOT_FOUND("PARKING_SLOT_NOT_FOUND", "Parking slot not found"),
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "Payment not found"),
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized access"),
    FORBIDDEN("FORBIDDEN", "Access forbidden"),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", "Rate limit exceeded");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
