package com.portfolio.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;
import static com.portfolio.repository.util.MemberUtil.isAdmin;

@Getter
/** 특정 글에 달린 댓글 단건에 대한 Response */
public class PostCommentResponse {

    //해당 댓글이 삭제되었는지 여부
    //false -> 삭제됨 대댓글 작성 불가
    //true -> 삭제 되지 않음 대댓글 작성 가능
    private final Boolean isEnabled;
    
    // 댓글 번호 
    private final Long id;

    //댓글 내용 
    private final String content;

    //댓글 작성자
    private final String username;

    //내가 작성한 댓글인지 여부
    private final Boolean isMyComment;
    
    //작성일 
    @JsonFormat(shape = STRING, pattern = "MM.dd HH:mm:ss")
    private final LocalDateTime lastModifiedDate;

    //해당 댓글에 달린 대댓글들
    private final List<ChildCommentResponse> childComments;

    private final Boolean isAdmin;


    public PostCommentResponse(Comment comment) {
        this.isEnabled = comment.getIsEnabled();
        this.id = comment.getId();
        this.content = getContent(comment);
        this.username = comment.getMember().getUsername();
        this.isMyComment = comment.getMember().getUsername().equals(getAuthenticatedUsername());
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.childComments = loadChildComments(comment);
        this.isAdmin = isAdmin();
    }

    //해당 댓글이 Soft Delete 처리 되었으면 내용이 "삭제된 댓글입니다" 로 출력됨
    private String getContent(Comment comment) {
        return comment.getIsEnabled() == true ? comment.getContent() : "삭제된 댓글입니다";
    }

    // 해당 댓글에 대댓글이 없는경우 빈 ArrayList 반환
    private static List<ChildCommentResponse> loadChildComments(Comment comment) {
        return comment.getChilds().isEmpty() == true ?
                new ArrayList<>() : getChildCommentList(comment);
    }


    // 해당 댓글에 대댓글들이 여러개 존재할떄, 그중에 Soft Delete 처리가 되지 않은 것들만 조회
    private static List<ChildCommentResponse> getChildCommentList(Comment comment) {
        return comment.getChilds().stream()
                .filter(c -> c.getIsEnabled() == true).map(ChildCommentResponse::new)
                .collect(Collectors.toList());
    }

}
