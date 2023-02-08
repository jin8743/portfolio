package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class AuthenticationFailedException extends DefaultException {

    private static final String MESSAGE = "인증에 대한 권한 실패\n" +
            "권한이 없거나 인증을 하지 못하였습니다.";

    public AuthenticationFailedException() {
        super(MESSAGE);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
