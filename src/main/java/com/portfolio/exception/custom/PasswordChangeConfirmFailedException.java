package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class PasswordChangeConfirmFailedException extends DefaultException {

    public static final String MESSAGE = "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.";

    public PasswordChangeConfirmFailedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
