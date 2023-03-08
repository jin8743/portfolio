package com.portfolio.request.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class CreateChildComment {

    @NotNull(message = "대댓글을 작성할 댓글번호가 입력되지 않았습니다")
    private Long parentCommentId;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 100, message = "댓글은 100글자 이하로 작성해주세요")
    private String content;


    @Builder
    public CreateChildComment(Long parentCommentId, String content) {
        this.parentCommentId = parentCommentId;
        this.content = content;
    }
}
