package com.company.exception;

public class WrongActionException extends RuntimeException {
    public WrongActionException(String message) {
        super(message);
    }

    public WrongActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
