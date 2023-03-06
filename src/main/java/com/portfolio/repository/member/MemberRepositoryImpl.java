package com.portfolio.repository.member;

import com.portfolio.domain.Member;
import com.portfolio.domain.QMember;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QMember.*;
import static com.portfolio.exception.custom.CustomNotFoundException.MEMBER_NOT_FOUND;


@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findActiveMemberByUsername(String username) {
        Member findMember = jpaQueryFactory.selectFrom(member)
                .where(member.isEnabled.eq(true))
                .where(member.username.eq(username))
                .fetchOne();

        if (findMember == null) {
            throw new CustomNotFoundException(MEMBER_NOT_FOUND);
        }
        return findMember;
    }

    @Override
    public Long countActiveMember() {
        Long count = jpaQueryFactory.select(member.count())
                .from(member)
                .where(member.isEnabled.eq(true))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public List<Member> findAllActiveMember() {
        return jpaQueryFactory.selectFrom(member)
                .where(member.isEnabled.eq(true))
                .fetch();
    }

}
