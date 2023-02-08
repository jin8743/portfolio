package com.portfolio.response;

import com.portfolio.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private final Long commentId;
    private final String content;
    private final String username;
    private final LocalDateTime lastModifiedDate;

//    @Builder
//    public CommentResponse(Long id, Post post, String content, Member member,
//                           LocalDateTime lastModifiedDate) {
//        this.id = id;
//        this.post = post;
//        this.content = content;
//        this.member = member;
//        this.lastModifiedDate = lastModifiedDate;
//    }

    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }


}
