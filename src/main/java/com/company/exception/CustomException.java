package com.company.exception;

public class CustomException extends RuntimeException {
    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}