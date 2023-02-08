package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class MemberNotFoundException extends DefaultException {

    private static final String MESSAGE = "사용자를 찾을수 없습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
