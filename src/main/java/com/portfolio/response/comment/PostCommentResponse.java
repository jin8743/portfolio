package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

@Data
public class PostCommentResponse {

    private final Long commentId;
    private final String content;
    private final String username;


    @JsonFormat(shape = STRING, pattern = "MM.dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;
    private final List<ChildCommentResponse> childComments;
    public PostCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.childComments = comment.getChilds().stream()
                .map(ChildCommentResponse::new)
                .collect(Collectors.toList());
    }
}
