package com.company.exception;

public class WrongIdException extends RuntimeException {
    public WrongIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongIdException(String message) {
        super(message);
    }
}
