package com.portfolio.request.validator;

import com.portfolio.exception.custom.CustomNotFoundException;

import static com.portfolio.exception.custom.CustomNotFoundException.COMMENT_NOT_FOUND;
import static com.portfolio.exception.custom.CustomNotFoundException.POST_NOT_FOUND;
import static com.portfolio.request.post.SearchPostsByBoard.MAX_SIZE;
import static java.lang.Math.*;

public class ConvertingStringValidator {

    /**
     * String "12345" ==> Long 12345 로  Type 변환 되지만
     * String "ab124sv" 같이 숫자 형식이 아닐경우 예외 발생 * */
    public static Long convertCommentId(String commentId) {
        try {
            return Long.parseLong(commentId);
        } catch (NumberFormatException e) {
            throw new CustomNotFoundException(COMMENT_NOT_FOUND);
        }
    }

    public static Long convertPostId(String postId) {
        try {
            return Long.parseLong(postId);
        } catch (NumberFormatException e) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
    }


    /** 페이징 처리시 페이지 처리
     * page 가 정상적인 숫자 형식 아닐경우 1 return
     * 음수이거나 0일 경우 1 return */
    public static Integer convertPage(String page) {
        try {
            return max(1, Integer.parseInt(page));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /** 페이징 처리시 한꺼번에 몇개를 보여줄지 처리
     *  정상적인 숫자 형식이 아닐경우 20 return
     *  음수이거나 0일경우 20 return
     *  MAX_SIZE 보다 클 경우 MAX_SIZE return
     */
    public static Integer convertSize(String str) {
        try {
            int size = Integer.parseInt(str);

            if (size <= 0) {
                return 20;
            } else return min(size, MAX_SIZE);

        } catch (NumberFormatException e) {
            return 20;
        }
    }
}
