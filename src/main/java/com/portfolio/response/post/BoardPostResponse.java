package com.portfolio.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.portfolio.domain.Post.loadCommentCount;


@Getter
/** 특정 게시판에 작성된 단건 글에 대한 Response */
public class BoardPostResponse {

    //게시판 한글 이름
    private final String nickname;

    //글 번호
    private final Long postId;

    //제목
    private final String title;

    //글 작성자
    private final String username;

    //글에 달린 총 댓글수 (Soft Delete 처리된 댓글의 수는 포함되지 않음)
    private final Integer totalComments;

    //글에 달린 총 졸아요수
    private final Integer totalLikes;

    //생성 날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime createdAt;

    public BoardPostResponse(Post post) {
        this.nickname = post.getBoard().getNickname();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.username = post.getMember().getUsername();
        this.totalComments = loadCommentCount(post);
        this.totalLikes = post.getLikes().size();
        this.createdAt = post.getCreatedAt();
    }
}
