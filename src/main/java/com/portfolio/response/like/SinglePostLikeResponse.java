package com.portfolio.response.like;

import lombok.Getter;

@Getter
/** 특정 글의 좋아요에 대한 Response */
public class SinglePostLikeResponse {

    // 글에 달린 총 좋아요 개수
    private final Long totalLikes;

    // 내가 좋아요를 눌렀는지 여부
    private final Boolean likedPost;

    public SinglePostLikeResponse(Long totalLikes, Boolean likedPost) {
        this.totalLikes = totalLikes;
        this.likedPost = likedPost;
    }
}
