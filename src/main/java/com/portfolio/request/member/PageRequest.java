package com.portfolio.request.member;

import lombok.Getter;

@Getter
public class PageRequest {

    private Integer page;

    public PageRequest(String page) {
        this.page = convert(page);
    }

    private Integer convert(String page) {
        try {
            return Integer.parseInt(page);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
