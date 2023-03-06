package com.portfolio.repository.like;


import com.portfolio.domain.Like;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.portfolio.domain.QLike.*;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Like loadExistingLike(Member member, Post post) {
        return jpaQueryFactory.selectFrom(like)
                .where(like.member.eq(member)
                        , like.post.eq(post))
                .fetchOne();
    }
}
