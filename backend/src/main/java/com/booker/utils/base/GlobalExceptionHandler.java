package com.booker.utils.base;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.validation.BindException;

import com.booker.constants.ErrorCodes;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            ServletRequestBindingException.class
    })
    public ResponseEntity<Response<Void>> handleValidationExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response<>(false, null, ErrorCodes.VALIDATION_FAILED));
    }

    /**
     * JSON/body non parsabile (es. JSON malformato).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response<>(false, null, ErrorCodes.INVALID_REQUEST_DATA));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new Response<>(false, null, ErrorCodes.OPERATION_NOT_ALLOWED));
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class
    })
    public ResponseEntity<Response<Void>> handleMediaTypeIssues(Exception ex) {
        HttpStatus status = (ex instanceof HttpMediaTypeNotAcceptableException)
            ? HttpStatus.NOT_ACCEPTABLE
            : HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        return ResponseEntity.status(status)
            .body(new Response<>(false, null, ErrorCodes.INVALID_REQUEST_DATA));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Response<>(false, null, ErrorCodes.DUPLICATE_RESOURCE));
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<Response<Void>> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response<>(false, null, ErrorCodes.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response<Void>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String code = mapStatusToErrorCode(status);
        return ResponseEntity.status(status)
                .body(new Response<>(false, null, code));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ErrorCodes.INVALID_REQUEST_DATA;
        HttpStatus status = ErrorCodes.RESOURCE_NOT_FOUND.equals(message) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new Response<>(false, null, message));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response<Void>> handleIllegalState(IllegalStateException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : ErrorCodes.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(false, null, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response<>(false, null, ErrorCodes.INTERNAL_SERVER_ERROR));
    }

    private static String mapStatusToErrorCode(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> ErrorCodes.INVALID_REQUEST_DATA;
            case UNAUTHORIZED -> ErrorCodes.UNAUTHORIZED_ACCESS;
            case FORBIDDEN -> ErrorCodes.INSUFFICIENT_PERMISSIONS;
            case NOT_FOUND -> ErrorCodes.RESOURCE_NOT_FOUND;
            case METHOD_NOT_ALLOWED -> ErrorCodes.OPERATION_NOT_ALLOWED;
            case CONFLICT -> ErrorCodes.DUPLICATE_RESOURCE;
            case TOO_MANY_REQUESTS -> ErrorCodes.RATE_LIMIT_EXCEEDED;
            case SERVICE_UNAVAILABLE -> ErrorCodes.SERVICE_UNAVAILABLE;
            default -> ErrorCodes.INTERNAL_SERVER_ERROR;
        };
    }
}
