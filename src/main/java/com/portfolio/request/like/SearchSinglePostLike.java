package com.portfolio.request.like;

import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
public class SearchSinglePostLike {

    private Long postId;

    public SearchSinglePostLike(String postId) {
        this.postId = convertPostId(postId);
    }
}
