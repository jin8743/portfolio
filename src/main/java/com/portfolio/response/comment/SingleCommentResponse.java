package com.portfolio.response.comment;

import com.portfolio.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SingleCommentResponse {

    private final Long id;
    private final String username;
    private final String postTitle;
    private final String content;

    private final LocalDateTime lastModifiedDate;

    private final List<SingleCommentResponse> childComments;

    public SingleCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getMember().getUsername();
        this.postTitle = comment.getPost().getTitle();
        this.content = comment.getContent();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.childComments = getChildComments(comment);
    }

    private List<SingleCommentResponse> getChildComments(Comment comment) {
        return comment.getChilds() == null ? new ArrayList<>() :
                comment.getChilds().stream()
                        .map(SingleCommentResponse::new)
                        .collect(Collectors.toList());
    }
}
