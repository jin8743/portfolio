package com.portfolio.exception;

public class DuplicateMemberException extends MyPortfolioException{

    private static final String MESSAGE = "이미 존재하는 아이디 입니다";

    public DuplicateMemberException() {
        super(MESSAGE);
    }

//    public DuplicateMemberException(String fieldName) {
//        super(MESSAGE);
//        addValidation(fieldName, MESSAGE);
//    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
