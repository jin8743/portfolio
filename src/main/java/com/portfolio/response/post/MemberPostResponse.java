package com.portfolio.response.post;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberPostResponse {

    private final Long postId;

    private final String title;

    private final Integer totalComments;

    private final String boardName;
    private final LocalDateTime lastModifiedDate;

    public MemberPostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.totalComments = post.getComments().size();
        this.boardName = post.getBoard().getBoardName();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
