package com.portfolio.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;


/**
 * 최상위 Exception
 */
@Getter
public abstract class DefaultException extends RuntimeException {


    public DefaultException(String message) {
        super(message);
    }


    public abstract int getStatusCode();


}
