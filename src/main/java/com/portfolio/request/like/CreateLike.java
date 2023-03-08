package com.portfolio.request.like;

import lombok.Builder;
import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
public class CreateLike {

    private Long postId;

    @Builder
    public CreateLike(String postId) {
        this.postId = convertPostId(postId);
    }
}
