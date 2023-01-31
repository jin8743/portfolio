package com.portfolio.repository.post;

import com.portfolio.domain.Board;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.post.PostRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QPost.*;
import static java.lang.Math.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Post> getList(Board board, int page, Member member) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(
                        memberEq(member),
                        boardEq(board))
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
