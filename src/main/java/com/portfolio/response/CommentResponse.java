package com.portfolio.response;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private final Long id;
    private final Post post;
    private final String content;
    private final Member member;
    private final LocalDateTime lastModifiedDate;

    @Builder
    public CommentResponse(Long id, Post post, String content, Member member,
                           LocalDateTime lastModifiedDate) {
        this.id = id;
        this.post = post;
        this.content = content;
        this.member = member;
        this.lastModifiedDate = lastModifiedDate;
    }

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.post = comment.getPost();
        this.content = comment.getContent();
        this.member = comment.getMember();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }


}
