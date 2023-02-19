package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class InvalidPasswordException extends DefaultException {

    private static final String MESSAGE = "비밀번호가 일치하지 않습니다.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
