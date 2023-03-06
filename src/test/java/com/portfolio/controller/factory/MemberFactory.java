package com.portfolio.controller.factory;

import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberFactory {

    @Autowired
    private  MemberRepository memberRepository;

    public Member createMember(String username) {
        Member member = Member.builder()
                .username(username)
                .password("password1234")
                .email(username + "@naver.com").build();
        memberRepository.save(member);
        return member;
    }
}
