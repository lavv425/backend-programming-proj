package com.booker.utils.base;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booker.constants.ErrorCodes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("${server.error.path:/error}")
    public ResponseEntity<Response<Void>> handleError(HttpServletRequest request) {
        int statusCode = extractStatusCode(request);
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            statusCode = status.value();
        }

        String code = mapStatusToErrorCode(status);
        return ResponseEntity.status(statusCode).body(new Response<>(false, null, code));
    }

    private static int extractStatusCode(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        try {
            return Integer.parseInt(statusCode.toString());
        } catch (NumberFormatException ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
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
