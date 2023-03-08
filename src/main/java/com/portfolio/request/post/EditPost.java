package com.portfolio.request.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@NoArgsConstructor
public class EditPost {

    @NotNull(message = "수정할 글 번호가 입력되지 않았습니다")
    private Long postId;

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 30, message = "제목은 30글자 이하로 작성해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    private Boolean commentsAllowed;

    @Builder
    public EditPost(Long postId, String title, String content, Boolean commentsAllowed) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.commentsAllowed = commentsAllowed;
    }
}
