package com.portfolio.repository.comment;

import com.portfolio.domain.*;
import com.portfolio.repository.comment.CommentRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QMember.*;
import static com.portfolio.domain.QPost.*;
import static java.lang.Math.max;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findByMember(int page, Member member) {
        return jpaQueryFactory
                .selectFrom(comment)
                .where(comment.member.eq(member))
                .leftJoin(comment.post, post).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .orderBy(comment.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    @Override
    public Comment findCommentWithChildCommentsById(Long id) {

        return jpaQueryFactory
                .selectFrom(comment)
                .where(comment.id.eq(id))
                .leftJoin(comment.childs).fetchJoin()
                .leftJoin(comment.post, post).fetchJoin()
                .fetchOne();
    }


    private Long getOffset(int page) {
        return (max(page, 1) - 1) * 20L;
    }


}
