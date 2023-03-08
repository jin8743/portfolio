package com.portfolio.response.post;

import com.portfolio.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.portfolio.domain.Post.loadTotalComments;

@Getter
public class MyLikedPostResponse {

    private final Long postId;
    private final String username;
    private final String title;
    private final Integer totalComments;
    private final String boardName;
    private final LocalDateTime lastModifiedDate;

    public MyLikedPostResponse(Post post) {
        this.postId = post.getId();
        this.username = post.getMember().getUsername();
        this.title = post.getTitle();
        this.totalComments = loadTotalComments(post);;
        this.boardName = post.getBoard().getBoardName();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
