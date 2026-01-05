package com.booker.utils.base;

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
    
}
