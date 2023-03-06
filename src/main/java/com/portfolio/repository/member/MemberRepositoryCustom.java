package com.portfolio.repository.member;

import com.portfolio.domain.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    Member findActiveMemberByUsername(String username);

    Long countActiveMember();

    List<Member> findAllActiveMember();
}
