package com.portfolio.exception.custom;

import com.portfolio.exception.MyPortfolioException;

public class InvalidLoginRequestException extends MyPortfolioException {

    private static final String MESSAGE = "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요.";


    @Override
    public int getStatusCode() {
        return 400;
    }

    public InvalidLoginRequestException() {
        super(MESSAGE);
    }
}
