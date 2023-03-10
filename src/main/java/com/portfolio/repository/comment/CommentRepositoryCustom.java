package com.portfolio.repository.comment;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;

import java.util.List;

public interface CommentRepositoryCustom {

    //내가 작성한 댓글과 대댓글 페이징 조회 (Soft Delete 처리된 댓글과 대댓글 조회 불가)
    List<Comment> findMyComments(int page, Member member);

    //특정 회원이 작성한 총 댓글수 (삭제된 댓글까지 전부 포함)
    Long findCommentCountByMember(Member member);

    //특정 글에 달린 댓글과 대댓글 페이징 조회
    List<Comment> findCommentsInPost(Post post, int page);

    Long findEnabledCommentCountInPost(Post post);

    //Soft Delete 처리 되지 않은 모든 댓글 개수 조회
    /** 테스트 케이스용 method */
    Long countActiveComments();

    //Soft Delete 처리되지 않은 모든 댓글 조회
    /** 테스트 케이스용 method */
    List<Comment> findAllActiveCommentWithPostAndMember();
}
