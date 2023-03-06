package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;

public class CustomBadRequestException extends DefaultException {

    public static final String INVALID_REQUIRED_PARAM = "필수 파라미터가 누락되었습니다.";
    public static final String USERNAME_OR_PASSWORD_NOT_PROVIDED = "아이디와 비밀번호는 공백일수 없습니다.";
    public static final String PASSWORD_NOT_PROVIDED = "비밀번호를 입력해주세요.";
    public static final String DUPLICATED_BOARDNAME = "이미 존재하는 게시판 이름입니다.";
    public static final String DUPLICATED_USERNAME = "이미 사용중이거나 탈퇴한 아이디입니다.";
    public static final String DUPLICATED_EMAIL = "이미 사용중이거나 탈퇴한 이메일입니다.";
    public static final String INVALID_LOGIN_INFO = "아이디/이메일 또는 비밀번호를 잘못 입력했습니다.";
    public static final String INVALID_LOGIN_FORMAT = "로그인 형식에 맞지 않습니다.";
    public static final String LOGIN_METHOD_NOT_SUPPORTED = "지원되지 않는 로그인 방식입니다.";
    public static final String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";
    public static final String SAME_PASSWORD = "현재 비밀번호와 동일합니다.";
    public static final String NOT_MATCHES_NEW_PASSWORD_CONFIRM = "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.";
    public static final String NOT_MATCHES_PASSWORD_CONFIRM = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
    public static final String COMMENTS_NOT_ALLOWED = "글 작성자가 댓글을 허용하지 않습니다.";

    public CustomBadRequestException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

}
