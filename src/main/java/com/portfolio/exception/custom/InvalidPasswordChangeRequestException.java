package com.portfolio.exception.custom;

import com.portfolio.exception.MyPortfolioException;

public class InvalidPasswordChangeRequestException extends MyPortfolioException {
    public static final String MESSAGE = "현재 비밀번호와 동일합니다.";

    public InvalidPasswordChangeRequestException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

}
