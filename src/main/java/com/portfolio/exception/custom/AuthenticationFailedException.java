package com.portfolio.exception.custom;

import com.portfolio.exception.MyPortfolioException;

public class AuthenticationFailedException extends MyPortfolioException {

    private static final String MESSAGE = "사용자 인증을 실패하였습니다.";

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
