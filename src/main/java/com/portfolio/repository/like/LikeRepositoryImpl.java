package com.portfolio.repository.like;


import com.portfolio.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QLike.*;
import static com.portfolio.domain.QPost.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long findLikeCountByPost(Post post) {
        Long count = jpaQueryFactory.select(like.count())
                .from(like)
                .where(like.post.eq(post))
                .fetchOne();

        return count != null ? count : 0;
    }

    @Override
    public Boolean pressedLikeOnThisPost(Post post) {
        return jpaQueryFactory.selectFrom(like)
                .where(like.post.eq(post))
                .where(like.member.username.eq(getAuthenticatedUsername()))
                .fetchOne() != null;
    }

    @Override
    public List<Post> findMyLikedPosts(int page) {
        return jpaQueryFactory.select(post)
                .from(like)
                .where(like.member.username.eq(getAuthenticatedUsername()))
                .orderBy(like.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }


    private Long getOffset(int page) {
        return (page - 1) * 20L;
    }
}
