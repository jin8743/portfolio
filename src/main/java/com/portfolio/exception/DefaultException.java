package com.portfolio.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


/**
 * 최상위 Exception
 */
@Getter
public abstract class DefaultException extends RuntimeException{

    private final Map<String, String> validation = new HashMap<>();

    public DefaultException(String message) {
        super(message);
    }

    public DefaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
