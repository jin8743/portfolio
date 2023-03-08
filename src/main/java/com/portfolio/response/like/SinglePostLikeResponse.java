package com.portfolio.response.like;

import lombok.Getter;

@Getter
public class SinglePostLikeResponse {

    private final Long totalLikes;

    private final Boolean likedPost;

    public SinglePostLikeResponse(Long totalLikes, Boolean likedPost) {
        this.totalLikes = totalLikes;
        this.likedPost = likedPost;
    }
}
