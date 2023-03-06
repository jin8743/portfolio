package com.portfolio.request.post;

import lombok.Getter;


import static java.lang.Math.*;

@Getter
public class BoardSearchRequest {

    private static final int MAX_SIZE = 50;

    /** 게시판 이름 */
    private String board;

    private Integer page;

    /** 글을 몇개 단위로 페이징 할것인지 */
    private Integer list_num;

    public BoardSearchRequest(String board, String page, String list_num) {
        this.board = board;
        this.page = convertPage(page);
        this.list_num = convertSize(list_num);
    }

    /**
     * String "12345" ==> Long 12345 로  Type 변환 되지만
     * String "ab124sv" 같이 정상적이지 않은 값이 입력될 경우
     * 1 page 가 보여질수 있도록 예외처리*/
    private Integer convertPage(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /** convertPage 와 동일하게 정상적이지 않는 값이 입력될 경우 예외처리 */
    private Integer convertSize(String str) {
        try {
            int size = Integer.parseInt(str);

            if (size <= 0) {
                return 20;
            } else return Math.min(size, 50);

        } catch (NumberFormatException e) {
            return 20;
        }
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(list_num, MAX_SIZE);
    }
}
