package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class PostNotFoundException  extends DefaultException {

    private static final String MESSAGE = "존재하지 않는 글입니다";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
