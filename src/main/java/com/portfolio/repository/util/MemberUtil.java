package com.portfolio.repository.util;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.MemberNotFoundException;
import com.portfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getContextMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

}
