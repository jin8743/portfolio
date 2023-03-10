package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import com.portfolio.repository.util.MemberUtil;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.portfolio.repository.util.MemberUtil.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Getter
/** 특정 댓글에 달린 대댓글에 대한 Response */
public class ChildCommentResponse {

    //부모 댓글의 댓글 번호
    private final Long parentCommentId;

    //댓글 번호
    private final Long Id;

    // 댓글 내용
    private final String content;

    //작성자
    private final String username;

    //내가 작성한 댓글인지 여부
    private final Boolean isMyComment;

    //현재 접속중인 사용자가 관리자인지 여부
    private final Boolean isAdmin;
    //작성일
    @JsonFormat(shape = STRING, pattern = "MM.dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public ChildCommentResponse(Comment comment) {
        this.parentCommentId = comment.getParent().getId();
        this.Id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.isMyComment = comment.getMember().getUsername().equals(getAuthenticatedUsername());
        this.createdAt = comment.getCreatedAt();
        this.isAdmin = isAdmin();
    }
}
