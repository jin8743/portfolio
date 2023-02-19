package com.portfolio.repository.post;

import com.portfolio.domain.*;
import com.portfolio.request.post.BoardSearchRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QMember.*;
import static com.portfolio.domain.QPost.*;
import static java.lang.Math.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Post findWithId(Long postId) {
        return jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .where(post.id.eq(postId))
                .join(post.member, member).fetchJoin()
                .join(post.board, board).fetchJoin()
                .join(post.comments, comment).fetchJoin()
                .join(comment.member, member).fetchJoin()
                .orderBy(comment.id.desc())
                .fetchOne();
    }

    @Override
    public Post findValidationPost(Long postId) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.id.eq(postId))
                .join(post.member, member).fetchJoin()
                .join(post.board, board).fetchJoin()
                .fetchOne();
    }


    @Override
    public List<Post> boardList(BoardSearchRequest searchRequest) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.board.boardName.eq(searchRequest.getId()))
                .join(post.member, member).fetchJoin()
                .join(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(searchRequest.getOffset())
                .limit(searchRequest.getList_num())
                .fetch();
    }

    @Override
    public List<Post> memberList(Member member, int page) {
        return jpaQueryFactory.selectFrom(post)
                .where(post.member.eq(member))
                .join(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }


    private BooleanExpression memberEq(Member member) {
        return member != null ? post.member.eq(member) : null;
    }

    private BooleanExpression boardEq(Board board) {
        return board != null ? post.board.eq(board) : null;
    }

    // page 에 음수가 들어갔을 경우 예외처리
    private Long getOffset(int page) {
        return (max(page, 1) - 1) * 20L;
    }

}
