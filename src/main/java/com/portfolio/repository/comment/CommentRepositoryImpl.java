package com.portfolio.repository.comment;

import com.portfolio.domain.*;
import com.portfolio.repository.comment.CommentRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QMember.*;
import static com.portfolio.domain.QPost.*;
import static java.lang.Math.max;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    //내가 작성한 댓글과 대댓글 페이징 조회 (Soft Delete 처리된 댓글과 대댓글 조회 불가)
    @Override
            //TODO  //<=========== 처리 해야됨
    public List<Comment> findMyComments(int page, Member member) {
        return jpaQueryFactory
                .selectFrom(comment)
                .where(comment.member.eq(member))
                .where(comment.isEnabled.eq(true))
                .leftJoin(comment.post, post).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .leftJoin(comment.parent).fetchJoin() //<===========
                .orderBy(comment.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    @Override
    public Long findCommentCountByMember(Member member) {
        return jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.member.eq(member))
                .fetchOne();
    }

    /**
     * 조회 하는 댓글의 종류
     * 1. Soft Delete 처리 되지 않은 댓글
     * 2. Soft Delete 처리가 되었으나 대댓글이 존재하는 댓글
     */
    @Override
    public List<Comment> findCommentsInPost(Post post, int page) {
        return jpaQueryFactory.selectFrom(comment)
                .where(comment.post.eq(post))
                .where(comment.parent.isNull())
                .where(comment.isEnabled.eq(true).or
                        ((comment.isEnabled.eq(false).and(comment.childs.isNotEmpty()))))
                .leftJoin(comment.member, member).fetchJoin()
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    @Override
    public Long findEnabledCommentCountInPost(Post post) {
        return jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(comment.isEnabled.eq(true))
                .where(comment.post.eq(post))
                .fetchOne();
    }

    @Override
    public Long countActiveComments() {
        return jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(comment.isEnabled.eq(true))
                .fetchOne();
    }

    //Soft Delete 처리되지 않은 모든 댓글 조회
    /** 테스트 케이스용 method */
    @Override
    public List<Comment> findAllActiveCommentWithPostAndMember() {
        return jpaQueryFactory.selectFrom(comment)
                .where(comment.isEnabled.eq(true))
                .leftJoin(comment.post, post).fetchJoin()
                .leftJoin(comment.member, member).fetchJoin()
                .fetch();
    }

    private Long getOffset(int page) {
        return (page - 1) * 20L;
    }
}
