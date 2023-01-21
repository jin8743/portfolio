package com.portfolio.exception.custom;

import com.portfolio.exception.MyPortfolioException;

public class InvalidJwtRequest extends MyPortfolioException {

    private static final String MESSAGE = "JWT 토큰값이 유효하지 않습니다";


    public InvalidJwtRequest() {
        super(MESSAGE);
    }

    public InvalidJwtRequest(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

}
