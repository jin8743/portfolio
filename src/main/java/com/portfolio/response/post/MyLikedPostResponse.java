package com.portfolio.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.portfolio.domain.Post.loadCommentCount;

@Getter
/** 내가 좋아요 누른 단건 글에 대한 Response */
public class MyLikedPostResponse {

    //글 번호
    private final Long postId;

    //제목
    private final String title;

    //작성자
    private final String writer;

    //글에 달린 총 댓글수 (Soft Delete 처리된 댓글의 수는 포함되지 않음)
    private final Integer totalComments;

    //글 작성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime createdAt;

    public MyLikedPostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getMember().getUsername();
        this.totalComments = loadCommentCount(post);
        this.createdAt = post.getCreatedAt();
    }
}
