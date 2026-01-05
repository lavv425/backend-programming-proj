package com.booker.constants;

/**
 * Standard error codes used throughout the application for consistent error handling.
 * These codes are mapped to HTTP status codes by ResponseEntityBuilder.
 */
public class ErrorCodes {
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";
    public static final String INVALID_REQUEST_DATA = "INVALID_REQUEST_DATA";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String OPERATION_NOT_ALLOWED = "OPERATION_NOT_ALLOWED";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String INSUFFICIENT_PERMISSIONS = "INSUFFICIENT_PERMISSIONS";
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
}
