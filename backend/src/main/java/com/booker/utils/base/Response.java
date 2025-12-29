package com.booker.utils.base;

public class Response<T> {
    public Boolean status;
    public T data;
    public String message;

    public Response(Boolean status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
    
}
