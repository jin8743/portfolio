package com.portfolio.request.post;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PostEditRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 30, message = "제목은 30글자 이하로 작성해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    private Boolean commentsAllowed;

    @Builder
    public PostEditRequest(String title, String content, Boolean commentsAllowed) {
        this.title = title;
        this.content = content;
        this.commentsAllowed = commentsAllowed;
    }
}
