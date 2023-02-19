package com.portfolio.repository.util;

import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import com.portfolio.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.portfolio.domain.MemberRole.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class MemberUtilTest {

    @Autowired
    MemberUtil memberUtil;

    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("SecurityContextHolder 에 저장된 member 객체 확인")
    @WithUserDetails(value = "username", userDetailsServiceBeanName = "customUserDetailsService",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void setMember() {
        Member member = Member.builder()
                .username("username")
                .password("password1234")
                .role(ROLE_MEMBER)
                .build();
        memberRepository.save(member);

        Member contextMember = memberUtil.getContextMember();
        assertEquals("username", contextMember.getUsername());
        assertEquals("password", contextMember.getPassword());
    }
}