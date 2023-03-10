package com.portfolio.repository.util;

import com.portfolio.domain.Board;
import com.portfolio.domain.Member;
import com.portfolio.exception.custom.*;
import com.portfolio.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.portfolio.exception.custom.CustomNotFoundException.*;


@RequiredArgsConstructor
@Component
/** 회원 조회용 Util Class */
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

    public static void validateUsername(String username) {
        if (getAuthenticatedUsername().equals(username) == false) {
            throw new AuthorizationFailedException();
        }
    }

    public static String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "";
    }

    /** 현재 접속중인 사용자가 관리자인지 확인 */
    public static Boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().toString().equals("[ROLE_ADMIN]");
    }

    /** 탈퇴한 계정을 포함해서 조회 */
    public Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() ->
                new CustomNotFoundException(MEMBER_NOT_FOUND));
    }

    /** 탈퇴한 계정은 포함하지 않고 조회*/
    public Member getActiveMember(String username) {
        return memberRepository.findActiveMemberByUsername(username);
    }

}
