package com.portfolio.repository.post;

import com.portfolio.domain.*;
import com.portfolio.request.post.SearchPostsByBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.Post.*;
import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QLike.like;
import static com.portfolio.domain.QMember.*;
import static com.portfolio.domain.QPost.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


//    /** 글 단건 조회 */
//    @Override
//    public Post findSinglePostWithId(Long postId) {
//
//        return jpaQueryFactory
//                .selectDistinct(post)
//                .from(post)
//                .where(post.id.eq(postId))
//                .leftJoin(post.member, member).fetchJoin()
//                .leftJoin(post.board, board).fetchJoin()
//                .leftJoin(post.comments, comment).fetchJoin()
//                .fetchOne();
//    }

    /** 특정 게시판에 작성된 글 페이징 조회 */
    @Override
    public List<Post> findPostsByBoard(SearchPostsByBoard searchRequest) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.board.boardName.eq(searchRequest.getBoard()))
                .leftJoin(post.board, board).fetchJoin()
                .leftJoin(post.member, member).fetchJoin()
                .orderBy(post.id.desc())
                .offset(searchRequest.getOffset())
                .limit(searchRequest.getSize())
                .fetch();
    }

    /** 특정 회원이 작성한 글 페이징 조회 */
    @Override
    public List<Post> findPostsByMember(Member member, int page) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(member))
                .leftJoin(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    /** 특정 회원이 작성한 총 글 갯수 조회 (Soft Delete 한 글도 포함) */
    //TODO count  확인
    @Override
    public Long findPostCountByMember(Member member) {
        return jpaQueryFactory.select(post.count())
                .from(post)
                .where(post.member.eq(member))
                .fetchOne();
    }

    /** 특정 회원이 댓글단 글 페이징 조회 */
    //TODO isEnabled 확인
    @Override
    public List<Post> findPostsCommentedMyMember(Member member, int page) {
        return jpaQueryFactory.select(post)
                .from(comment)
                .where(comment.member.eq(member))
                .where(post.isEnabled.eq(true))
                .orderBy(comment.id.desc())
                .fetch();
    }

    /** 현재 접속중인 회원이 좋아요 누른 글 페이징 조회 */
    @Override
    //TODO isEnabled 확인
    public List<Post> findMyLikedPosts(int page) {
        return jpaQueryFactory.select(post)
                .from(like)
                .where(like.member.username.eq(getAuthenticatedUsername()))
                .orderBy(like.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    @Override
    public List<Post> findAllPosts(int page) {
        return jpaQueryFactory.selectFrom(post)
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    private Long getOffset(int page) {
        return (page - 1) * 20L;
    }
}
