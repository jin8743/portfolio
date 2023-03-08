package com.portfolio.request.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
@NoArgsConstructor
public class CreateComment {

    @NotNull(message = "글 번호가 입력되지 않았습니다")
    private Long postId;

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 100, message = "댓글은 100글자 이하로 작성해주세요")
    private String content;

    @Builder
    public CreateComment(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
