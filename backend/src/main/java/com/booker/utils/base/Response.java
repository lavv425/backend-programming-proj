package com.booker.utils.base;

import java.util.Objects;

/**
 * A generic response wrapper that standardizes API responses across the application.
 * Contains a status flag, data payload, and message to communicate operation results.
 *
 * @param <T> the type of data being returned in the response
 */
public class Response<T> {
    public Boolean status;
    public T data;
    public String message;

    /**
     * Creates a new response with the given status, data, and message.
     *
     * @param status whether the operation was successful
     * @param data the response data (can be null)
     * @param message a message describing the result (typically a code from ErrorCodes or SuccessCodes)
     */
    public Response(Boolean status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /**
     * Checks equality between this response and another object.
     * @param o the object to compare with
     * @return true if both responses are equal in status, data, and message; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response<?> response = (Response<?>) o;
        return Objects.equals(status, response.status) &&
               Objects.equals(data, response.data) &&
               Objects.equals(message, response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data, message);
    }
    
}
