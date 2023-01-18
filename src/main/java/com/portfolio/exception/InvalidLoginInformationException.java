package com.portfolio.exception;

public class InvalidLoginInformationException extends MyPortfolioException {

    public InvalidLoginInformationException(String message) {
        super(message);
    }

    public InvalidLoginInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return 0;
    }
}
