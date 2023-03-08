package com.portfolio.request.like;

import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
public class CancelLike {

    private Long postId;

    public CancelLike(String postId) {
        this.postId = convertPostId(postId);
    }
}
