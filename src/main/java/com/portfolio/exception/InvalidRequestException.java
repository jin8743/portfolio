package com.portfolio.exception;

public class InvalidRequestException extends MyPortfolioException {

    private static final String MESSAGE ="잘못된 요청입니다";

    public InvalidRequestException() {
        super(MESSAGE);
    }

//    public InvalidRequestException(String fieldName) {
//        super(MESSAGE);
//        addValidation(fieldName, MESSAGE);
//    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
