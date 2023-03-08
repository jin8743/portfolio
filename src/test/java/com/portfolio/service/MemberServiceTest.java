package com.portfolio.service;

import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.SignUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clear() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("/join 요청시 DB에 값이 저장된다")
    void test1() {
        SignUp signUp = SignUp.builder()
                .username("username1234")
                .password("password1234")
                .build();

        memberService.saveNewMember(signUp);
        Member member = memberRepository.findByUsername(signUp.getUsername()).get();

        assertEquals(1L, memberRepository.count());
        assertEquals("username1234", member.getUsername());
        assertEquals("ROLE_MEMBER", member.getRole().toString());
        passwordEncoder.matches("password1234", member.getPassword());
    }



}