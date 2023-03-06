package com.portfolio.request.member;

import lombok.Getter;

@Getter
public class PageRequest {

    private Integer page;

    public PageRequest(String page) {
        this.page = convert(page);
    }


    /**
     * String "12345" ==> Long 12345 로  Type 변환 되지만
     * String "ab124sv" 같이 정상적이지 않은 값이 입력될 경우
     * 1 page 가 보여질수 있도록 예외처리*/
    private Integer convert(String page) {
        try {
            return Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
