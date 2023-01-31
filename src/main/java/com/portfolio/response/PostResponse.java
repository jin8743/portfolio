package com.portfolio.response;

import com.portfolio.domain.Board;
import com.portfolio.domain.Comment;
import com.portfolio.domain.Post;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final List<Comment> comments;

    private final Board board;
    private final LocalDateTime lastModifiedDate;

    @Builder
    public PostResponse(Long id, String title, String content, List<Comment> comments, Board board,
                        LocalDateTime lastModifiedDate) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.comments = comments != null ? comments : new ArrayList<>();
        this.board = board;
        this.lastModifiedDate = lastModifiedDate;
    }

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.comments = post.getComments();
        this.board = post.getBoard();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
