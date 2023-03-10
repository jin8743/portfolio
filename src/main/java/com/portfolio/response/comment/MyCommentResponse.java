package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;
import static com.portfolio.repository.util.MemberUtil.isAdmin;

@Getter
/** 내가 작성한 댓글 단건에 대한 Response */
public class MyCommentResponse {

    // 댓글 번호
    private final Long commentId;

    // 댓글 내용
    private final String content;

    // 글 번호
    private final Long postId;

    // 글이 삭제되었는지 여부 (삭제된 경우 글 제목이 "삭제된 글입니다" 로 표시됨)
    //true -> 글 삭제되지 않음
    //false -> 글 삭제됨
    private final Boolean postEnabled;

    // 글 제목
    private final String postTitle;

    // 댓글이 작성된 게시판 별칭
    private final String nickname;

    // 작성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    //대댓글인지 여부
    // true -> 대댓글임
    // false -> 대댓글이 아닌 일반 댓글임
    private final Boolean isChildComment;

    public MyCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPost().getId();
        this.postEnabled = comment.getPost().getIsEnabled();
        this.postTitle = getTitle(comment);
        this.nickname = comment.getPost().getBoard().getBoardName();
        this.createdAt = comment.getCreatedAt();
        this.isChildComment = isChildComment(comment);
    }

    private static String getTitle(Comment comment) {
        return comment.getPost().getIsEnabled() == true ?
                comment.getPost().getTitle() : "삭제된 글입니다";
    }

    // 부모 댓글은 상위 댓글이 없으므로
    // getParent() 값이 null 이 되므로 false 반환
    private boolean isChildComment(Comment comment) {
        return comment.getParent() != null;
    }

}
