package com.booker.utils.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.booker.constants.ErrorCodes;
import com.booker.constants.SuccessCodes;

/**
 * Builds Spring ResponseEntity objects from Response wrappers, automatically mapping
 * message codes to appropriate HTTP status codes.
 */
public class ResponseEntityBuilder {

    /**
     * Converts a Response into a ResponseEntity with the correct HTTP status code
     * based on the message code (from ErrorCodes or SuccessCodes).
     *
     * @param response the response to convert
     * @param <T> the type of data in the response
     * @return a ResponseEntity with the appropriate HTTP status
     */
    public static <T> ResponseEntity<Response<T>> build(Response<T> response) {
        if (response == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(false, null, ErrorCodes.INTERNAL_SERVER_ERROR));
        }

        HttpStatus status = mapMessageToStatus(response.message, response.status);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(response);
    }

    private static HttpStatus mapMessageToStatus(String message, Boolean success) {
        if (message == null) {
            return success == Boolean.TRUE ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // ErrorCodes mapping
        return switch (message) {
            case ErrorCodes.RESOURCE_NOT_FOUND, ErrorCodes.USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrorCodes.UNAUTHORIZED_ACCESS, ErrorCodes.TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case ErrorCodes.INSUFFICIENT_PERMISSIONS, ErrorCodes.OPERATION_NOT_ALLOWED -> HttpStatus.FORBIDDEN;
            case ErrorCodes.INVALID_REQUEST_DATA, ErrorCodes.VALIDATION_FAILED, ErrorCodes.INVALID_CREDENTIALS ->
                    HttpStatus.BAD_REQUEST;
            case ErrorCodes.DUPLICATE_RESOURCE, ErrorCodes.EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case ErrorCodes.RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case ErrorCodes.SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case ErrorCodes.INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;

            // SuccessCodes mapping
            case SuccessCodes.USER_REGISTERED, SuccessCodes.APPOINTMENT_BOOKED, SuccessCodes.PAYMENT_PROCESSED,
                 SuccessCodes.REVIEW_SUBMITTED, SuccessCodes.ROLE_CREATED, SuccessCodes.SERVICE_ADDED ->
                    HttpStatus.CREATED;

            case SuccessCodes.USER_DELETED, SuccessCodes.PROFILE_IMAGE_DELETED, SuccessCodes.PAYMENT_DELETED,
                 SuccessCodes.REVIEW_DELETED, SuccessCodes.ROLE_DELETED, SuccessCodes.SERVICE_DELETED,
                 SuccessCodes.APPOINTMENT_CANCELLED ->
                    HttpStatus.NO_CONTENT;

            // Default: OK per success, INTERNAL_SERVER_ERROR per errori sconosciuti
            default -> success == Boolean.TRUE ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
