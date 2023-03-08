package com.portfolio.response.post;

import com.portfolio.domain.Post;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static com.portfolio.domain.Post.loadTotalComments;

@Getter
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
        this.totalComments = loadTotalComments(post);
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
