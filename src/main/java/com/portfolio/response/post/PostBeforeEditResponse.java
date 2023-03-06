package com.portfolio.response.post;

import com.portfolio.domain.Post;
import lombok.Data;

@Data
public class PostBeforeEditResponse {

    private final Long postId;
    private final String title;
    private final String content;
    private final Boolean commentsAllowed;

    public PostBeforeEditResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.commentsAllowed = post.getCommentsAllowed();
    }
}
