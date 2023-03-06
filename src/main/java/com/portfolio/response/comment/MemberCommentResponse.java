package com.portfolio.response.comment;

import com.portfolio.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberCommentResponse {

    private final Long commentId;
    private final String content;
    private final String postTitle;
    private final String boardName;
    private final LocalDateTime lastModifiedDate;
    private final Boolean isChildComment;


    public MemberCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.postTitle = getPostTitle(comment);
        this.boardName = getBoardName(comment);
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.isChildComment = isChildComment(comment);
    }

    private String getPostTitle(Comment comment) {
        return isChildComment(comment) ?
                comment.getParent().getPost().getTitle() : comment.getPost().getTitle();
    }

    private String getBoardName(Comment comment) {
        return isChildComment(comment) ?
                comment.getParent().getPost().getBoard().getBoardName() : comment.getPost().getBoard().getBoardName();
    }
    // 부모 댓글은 상위 댓글이 없으므로 false 반환
    // 대댓글은 부모 댓글이 존재하므로 true 반환
    private boolean isChildComment(Comment comment) {
        if (comment.getParent() == null) {
            return false;
        } else {
            return true;
        }
    }

}
