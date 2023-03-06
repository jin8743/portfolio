package com.portfolio.response.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Post;
import com.portfolio.response.comment.MemberCommentResponse;
import com.portfolio.response.comment.PostCommentResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SinglePostResponse {

    private final String boardName;
    private final Long postId;
    private final String title;
    private final String writer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;
    private final String content;
    private final Integer totalComments;
    private final Integer totalLikes;

    private final Boolean myPost;
    private final List<PostCommentResponse> comments;


    public SinglePostResponse(Post post, String username) {
        this.boardName = post.getBoard().getBoardName();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getMember().getUsername();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.content = post.getContent();
        this.totalComments = post.getComments().size();
        this.totalLikes = post.getLikes();
        this.myPost = post.getMember().getUsername().equals(username);
        this.comments = post.getComments().stream()
                .map(PostCommentResponse::new)
                .collect(Collectors.toList());
    }
}
