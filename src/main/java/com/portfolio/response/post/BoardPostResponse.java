package com.portfolio.response.post;

import com.portfolio.domain.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardPostResponse {

    private final Long postId;
    private final String title;
    private final String username;
    private final Integer totalComments;
    private final LocalDateTime lastModifiedDate;

    public BoardPostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.username = post.getMember().getUsername();
        this.totalComments = post.getComments().size();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
