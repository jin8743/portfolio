package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class InvalidRequestException extends DefaultException {

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
