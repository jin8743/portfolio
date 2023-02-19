package com.portfolio.response;

import com.portfolio.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberCommentResponse {

    private final Long commentId;
    private final String content;
    private final String boardName;
    private final Long postId;
    private final LocalDateTime lastModifiedDate;


    public MemberCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.boardName = comment.getPost().getBoard().getBoardName();
        this.postId = comment.getPost().getId();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }
}
