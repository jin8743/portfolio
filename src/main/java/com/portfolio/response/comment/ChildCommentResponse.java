package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Getter
public class ChildCommentResponse {

    private final Long parentCommentId;
    private final Long Id;
    private final String content;
    private final String username;
    //내가 작성한 댓글인지 여부
    private final Boolean myComment;
    @JsonFormat(shape = STRING, pattern = "MM.dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;

    public ChildCommentResponse(Comment comment) {
        this.parentCommentId = comment.getParent().getId();
        this.Id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.myComment = comment.getMember().getUsername().equals(getAuthenticatedUsername());
        this.lastModifiedDate = comment.getLastModifiedDate();
    }
}
