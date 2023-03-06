package com.portfolio.repository.util;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.*;
import com.portfolio.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
/** 조회용 Util Class */
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getContextMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            return memberRepository.findActiveMemberByUsername(username);
        } else {
            throw new AuthenticationFailedException();
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "";
    }

    public Member getActiveMember(String username) {
        return memberRepository.findActiveMemberByUsername(username);
    }


}
