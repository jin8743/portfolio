package com.portfolio.exception.custom;

import com.portfolio.exception.DefaultException;


public class CustomNotFoundException extends DefaultException {


    public static final String COMMENT_NOT_FOUND = "댓글이 존재하지 않거나 삭제되었습니다";

    public static final String POST_NOT_FOUND = "게시글이 존재하지 않거나 삭제되었습니다";

    public static final String MEMBER_NOT_FOUND = "사용자를 찾을수 없습니다.";

    public static final String BOARD_NOT_FOUND = "알수 없는 게시판 이름 입니다";


    public CustomNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }

}
