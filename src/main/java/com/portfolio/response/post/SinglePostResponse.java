package com.portfolio.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Post;
import com.portfolio.repository.util.MemberUtil;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.portfolio.repository.util.MemberUtil.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Getter
/** 글 단건에 대한  Response */
public class SinglePostResponse {

    // 게시판 영문 이름
    private final String boardName;

    //게시판 한글 이름
    private final String nickname;

    //글 번호
    private final Long postId;

    //글 제목
    private final String title;

    //글 작성자
    private final String writer;

    //마지막 수정 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;

    //글 내용
    private final String content;

    //내가 작성한 글인지 여부
    private final Boolean myPost;

    //해당 글이 댓글 작성을 허용하는지
    private final Boolean commentsAllowed;

    //접속중인 사용자가 관리자 계정인지 여부
    private final Boolean isAdmin;

    @Builder
    public SinglePostResponse(Post post) {
        this.boardName = post.getBoard().getBoardName();
        this.nickname = post.getBoard().getNickname();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getMember().getUsername();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.content = post.getContent();
        this.myPost = post.getMember().getUsername().equals(getAuthenticatedUsername());
        this.commentsAllowed = post.getCommentsAllowed();
        this.isAdmin = isAdmin();
    }
}
