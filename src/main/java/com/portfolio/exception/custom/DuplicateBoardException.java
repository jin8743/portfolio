package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class DuplicateBoardException extends DefaultException {

    private static final String MESSAGE = "이미 존재하는 게시판 이름입니다.";

    public DuplicateBoardException() {
        super(MESSAGE);
    }

    public DuplicateBoardException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
