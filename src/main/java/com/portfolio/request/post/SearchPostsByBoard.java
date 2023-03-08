package com.portfolio.request.post;

import lombok.Getter;


import static com.portfolio.request.validator.ConvertingStringValidator.convertPage;
import static com.portfolio.request.validator.ConvertingStringValidator.convertSize;
import static java.lang.Math.*;

@Getter
public class SearchPostsByBoard {

    public static final int MAX_SIZE = 50;

    /** 게시판 이름 */
    private String board;

    private Integer page;

    /** 글을 몇개 단위로 페이징 할것인지 */
    private Integer size;

    public SearchPostsByBoard(String board, String page, String size) {
        this.board = board;
        this.page = convertPage(page);
        this.size = convertSize(size);
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}
