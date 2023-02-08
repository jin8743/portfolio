package com.portfolio.request.post;

import lombok.Getter;


import static java.lang.Math.*;

@Getter
public class BoardSearchRequest {

    private static final int MAX_SIZE = 100;

    private String id;

    private Integer page;

    private Integer list_num;

    public BoardSearchRequest(String id, String page, String list_num) {
        this.id = id;
        this.page = convertPage(page);
        this.list_num = convertSize(list_num);
    }

    private Integer convertPage(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private Integer convertSize(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(list_num, MAX_SIZE);
    }
}
