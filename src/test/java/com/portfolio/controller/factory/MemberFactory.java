package com.portfolio.controller.factory;

import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import com.portfolio.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.portfolio.domain.MemberRole.*;

@Component
public class MemberFactory {

    @Autowired
    private  MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Member createMember(String username) {
        Member member = Member.builder()
                .username(username)
                .password(encoder.encode("password1234!"))
                .email(username + "@naver.com").build();
        memberRepository.save(member);
        return member;
    }

    public Member createAdmin(String username) {
        Member member = Member.builder()
                .username(username)
                .role(ROLE_ADMIN)
                .password(encoder.encode("password1234!"))
                .email(username + "@naver.com").build();
        memberRepository.save(member);
        return member;
    }
}
