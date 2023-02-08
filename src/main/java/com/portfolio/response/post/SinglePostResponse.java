package com.portfolio.response.post;

import com.portfolio.domain.Post;
import com.portfolio.response.CommentResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SinglePostResponse {

    private final Long postId;
    private final String title;
    private final String writer;
    private final String content;
    private final List<CommentResponse> comments;
    private final Integer totalComments;
    private final String boardName;
    private final LocalDateTime lastModifiedDate;


//    @Builder
//    public PostResponse(Long id, String title, String content, List<Comment> comments, Long totalComments, Board board,
//                        LocalDateTime lastModifiedDate) {
//
//        this.id = id;
//        this.title = title;
//        this.content = content;
//        this.comments = comments != null ? comments : new ArrayList<>();
//        this.totalComments = totalComments;
//        this.board = board;
//        this.lastModifiedDate = lastModifiedDate;
//    }


    public SinglePostResponse(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getMember().getUsername();
        this.content = post.getContent();
        this.comments = post.getComments().stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
        this.totalComments = post.getComments().size();
        this.boardName = post.getBoard().getBoardName();
        this.lastModifiedDate = post.getLastModifiedDate();
    }
}
