package com.portfolio.response.post;

import com.portfolio.domain.Post;

import java.time.LocalDateTime;

public class MemberLikedPostResponse {

    private final Long postId;
    private final String username;
    private final String title;
    private final Integer totalComments;
    private final String boardName;
    private final LocalDateTime lastModifiedDate;

    public MemberLikedPostResponse(Post post) {
        this.postId = post.getId();
        this.username = post.getMember().getUsername();
        this.title = post.getTitle();
        this.totalComments = post.getComments().size();
        this.boardName = post.getBoard().getBoardName();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
