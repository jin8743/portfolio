package com.portfolio.request.comment;

import com.portfolio.exception.custom.CustomNotFoundException;
import lombok.Getter;

import static com.portfolio.exception.custom.CustomNotFoundException.COMMENT_NOT_FOUND;

@Getter
public class EditCommentIdRequest {

    private Long id;

    public EditCommentIdRequest(String id) {
        this.id = convert(id);
    }

    /**
     * String "12345" ==> Long 12345 로  Type 변환 되지만
     * String "ab124sv" 같이 정상적이지 않은 값이 입력될 경우 예외 발생 * */
    private Long convert(String no) {
        try {
            return Long.parseLong(no);
        } catch (NumberFormatException e) {
            throw new CustomNotFoundException(COMMENT_NOT_FOUND);
        }
    }
}
