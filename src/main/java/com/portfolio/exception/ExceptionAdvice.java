package com.portfolio.exception;

import com.portfolio.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;


@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<ErrorResponse> defaultExceptionHandler(DefaultException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .build();

        ResponseEntity<ErrorResponse> response = ResponseEntity.status(statusCode)
                .body(body);

        return response;
    }

    /** 400 */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse exceptionHandler(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다")
                .build();

        e.getFieldErrors().forEach(fieldError -> response.addValidation(fieldError.getField(), fieldError.getDefaultMessage()));
        return response;
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse exceptionHandler(HttpMessageNotReadableException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("서버에 전송한 정보가 형식에 맞지 않습니다")
                .build();
        return response;
    }

    /** 415 */
    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse exceptionHandler(HttpMediaTypeNotSupportedException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code("415")
                .message("지원하지 않는 미디어 유형입니다 가능한 유형: " + "APPLICATION_JSON")
                .build();
        return response;
    }

    /** 405 */
    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse exceptionHandler(HttpRequestMethodNotSupportedException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code("405")
                .message("지원되지 않는 방식입니다. 요청가능한 형식: " + Arrays.toString(e.getSupportedMethods()))
                .build();
        return response;
    }
}
