package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Getter
public class PostCommentResponse {

    private final Long id;
    private final String content;
    private final String username;
    private final Boolean myComment;

    @JsonFormat(shape = STRING, pattern = "MM.dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;
    private final List<ChildCommentResponse> childComments;
    public PostCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.myComment = comment.getMember().getUsername().equals(getAuthenticatedUsername());
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.childComments = comment.getChilds().stream()
                .map(ChildCommentResponse::new)
                .collect(Collectors.toList());
    }
}
