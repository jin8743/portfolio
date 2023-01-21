package com.portfolio.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


/**
 * 최상위 Exception
 */
@Getter
public abstract class MyPortfolioException extends RuntimeException{

    public abstract int getStatusCode();

    private final Map<String, String> validation = new HashMap<>();

    public MyPortfolioException(String message) {
        super(message);
    }

    public MyPortfolioException(String message, Throwable cause) {
        super(message, cause);
    }

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
