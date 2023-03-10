package com.portfolio.repository.member;

import com.portfolio.domain.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    Member findActiveMemberByUsername(String username);

    Long countActiveMember();

    List<Member> findAllActiveMember();


    //관리자용 회원 정보 조회 기능 (탈퇴한 회원도 조회 가능)
    List<Member> findMemberList(int page);
}
