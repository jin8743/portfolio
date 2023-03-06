package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class AuthenticationFailedException extends DefaultException {

    public static final String MESSAGE = "인증을 하지 못하였습니다. 로그인 후 이용해주세요";

    public AuthenticationFailedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
