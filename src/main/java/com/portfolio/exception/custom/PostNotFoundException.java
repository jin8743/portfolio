package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class PostNotFoundException  extends DefaultException {

    private static final String MESSAGE = "게시글이 존재하지 않거나 삭제되었습니다";

    public PostNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
