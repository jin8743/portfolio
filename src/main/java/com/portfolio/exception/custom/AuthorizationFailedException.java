package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class AuthorizationFailedException extends DefaultException {

    public static final String MESSAGE = "해당 권한이 없습니다";

    public AuthorizationFailedException() {
        super(MESSAGE);
    }

    public AuthorizationFailedException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }

}
