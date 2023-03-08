package com.portfolio.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Post;
import com.portfolio.response.comment.PostCommentResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.portfolio.domain.Post.loadTotalComments;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Getter
public class SinglePostResponse {

    // 게시판 영문 이름
    private final String boardName;

    //게시판 한글 이름
    private final String nickName;

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

    @Builder
    public SinglePostResponse(Post post) {
        this.boardName = post.getBoard().getBoardName();
        this.nickName = post.getBoard().getNickname();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getMember().getUsername();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.content = post.getContent();
        this.myPost = post.getMember().getUsername().equals(getAuthenticatedUsername());
    }
}
