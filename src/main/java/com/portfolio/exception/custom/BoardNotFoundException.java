package com.portfolio.exception.custom;

import com.portfolio.exception.MyPortfolioException;

public class BoardNotFoundException extends MyPortfolioException {

    private static final String MESSAGE = "존재하지 않는 게시판 입니다";

    public BoardNotFoundException() {
        super(MESSAGE);
    }

    public BoardNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
