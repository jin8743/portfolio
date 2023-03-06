package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
public class ChildCommentResponse {

    private final Long parentId;

    private final Long commentId;

    private final String content;

    private final String username;
    @JsonFormat(shape = STRING, pattern = "MM.dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;

    public ChildCommentResponse(Comment comment) {
        this.parentId = comment.getParent().getId();
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }
}
