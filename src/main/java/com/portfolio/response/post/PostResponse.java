package com.portfolio.response.post;

import com.portfolio.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.portfolio.domain.Post.loadCommentCount;

@Getter
/** 전체글 조회시 글 단건에 대한 Response */
public class PostResponse {

    //글 번호
    private final Long postId;

    //게시판 별칭
    private final String nickname;

    //글 제목
    private final String title;

    //총 댓글 (Soft Delete 처리된 댓글은 포함되지 않음)
    private final Integer totalComments;

    //작성자
    private final String writer;

    //작성일
    private final LocalDateTime createdAt;

    public PostResponse(Post post) {
        this.postId = post.getId();
        this.nickname = post.getBoard().getNickname();
        this.title = post.getTitle();
        this.totalComments = loadCommentCount(post);
        this.writer = post.getMember().getUsername();
        this.createdAt = post.getCreatedAt();
    }
}
