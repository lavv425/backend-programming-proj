package com.booker.utils.base;

public class Response<T> {
    public boolean status;
    public T data;
    public String message;

    public Response(boolean status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
    
}
