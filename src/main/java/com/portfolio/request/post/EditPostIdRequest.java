package com.portfolio.request.post;

import com.portfolio.exception.custom.CustomNotFoundException;
import lombok.Getter;

import static com.portfolio.exception.custom.CustomNotFoundException.POST_NOT_FOUND;

/** 수정할 글 조회용 Class */
@Getter
public class EditPostIdRequest {
    private Long id;

    public EditPostIdRequest(String id) {
        this.id = convert(id);
    }

    /**
     * String "12345" ==> Long 12345 로  Type 변환 되지만
     * String "ab124sv" 같이 정상적이지 않은 값이 입력될 경우 예외 발생 * */
    private Long convert(String no) {
        try {
            return Long.parseLong(no);
        } catch (NumberFormatException e) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
    }
}
