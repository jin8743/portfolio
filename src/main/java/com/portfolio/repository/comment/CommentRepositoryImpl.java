package com.portfolio.repository.comment;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.QBoard;
import com.portfolio.domain.QPost;
import com.portfolio.repository.comment.CommentRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QPost.*;
import static java.lang.Math.max;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Comment> getList(int page, Member member) {
        return jpaQueryFactory
                .selectFrom(comment)
                .where(comment.member.eq(member))
                .join(comment.post, post).fetchJoin()
                .join(post.board, board).fetchJoin()
                .orderBy(comment.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }


    private Long getOffset(int page) {
        return (max(page, 1) - 1) * 20L;
    }

}
