package com.portfolio.request.comment;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 100, message = "댓글은 100글자 이하로 작성해주세요")
    private String content;

    @Builder
    public CommentCreateRequest(String content) {
        this.content = content;
    }



}
