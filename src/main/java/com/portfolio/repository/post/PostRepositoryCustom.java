package com.portfolio.repository.post;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.request.post.SearchPostsByBoard;
import lombok.extern.java.Log;

import java.util.List;

public interface PostRepositoryCustom {


    //특정 게시판에 작성된 글 페이징 조회
    List<Post> findPostsByBoard(SearchPostsByBoard searchPostsByBoard);

    //특정 회원이 작성한 글 페이징 조회
    List<Post> findPostsByMember(Member member, int page);

    //특정 회원이 작성한 총 글 갯수 조회 (Soft Delete 처리된 글 포함)
    Long findPostCountByMember(Member member);

    // 특정 회원이 댓글단 글 페이징 조회 (Soft Delete 처리된 글은 조회되지 않음, 탈퇴한 회원이 댓글단글 조회 불가)
    List<Post> findPostsCommentedMyMember(Member member, int page);

    // 현재 접속중인 회원이 좋아요를 누른 글 페이징 조회 (Soft Delete 처리된 글은 조회되지 않음)
    List<Post> findMyLikedPosts(int page);

    // 작성된 전체글 페이징 조회 (Soft Delete 처리된 글은 조회되지 않음)
    List<Post> findAllPosts(int page);
}
