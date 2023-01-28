package com.portfolio.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentEditRequest {

    @NotBlank(message = "댓글 내용을 입력하지 않았습니다")
    @Size(max = 500, message = "댓글은 500글자 이하로 작성해주세요")
    private String content;
}
