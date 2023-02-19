package com.portfolio.response.post;

import com.portfolio.domain.Post;
import com.portfolio.response.MemberCommentResponse;
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
    private final LocalDateTime lastModifiedDate;
    private final String content;
    private final Integer totalComments;
    private final List<MemberCommentResponse> comments;


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
        this.boardName = post.getBoard().getBoardName();
        this.postId = post.getId();
        this.title = post.getTitle();
        this.writer = post.getMember().getUsername();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.content = post.getContent();
        this.totalComments = post.getComments().size();
        this.comments = post.getComments().stream()
                .map(MemberCommentResponse::new)
                .collect(Collectors.toList());
    }
}
