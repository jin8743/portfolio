package com.portfolio.request.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.portfolio.request.validator.ConvertingStringValidator.convertCommentId;

@Getter
@NoArgsConstructor
public class EditComment {

    @NotNull(message = "수정할 댓글번호가 입력되지 않았습니다")
    private Long commentId;

    @NotBlank(message = "내용을 입력하지 않았습니다")
    @Size(max = 500, message = "댓글은 500글자 이하로 작성해주세요")
    private String content;

    @Builder
    public EditComment(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }
}
