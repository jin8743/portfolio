package com.portfolio.repository.util;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.*;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.member.PasswordChangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getContextMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            return getMember(username);
        } else {
            throw new AuthenticationFailedException();
        }
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        throw new AuthorizationFailedException();
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    public void memberExists(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new DuplicateMemberException();
        }
    }

    public static String validatePassword(Member member, PasswordChangeRequest request,
                                          PasswordEncoder passwordEncoder) {

        /** DB에 저장된 비밀번호와 입력한 비밀번호가 일치하지 않을경우 예외 발생 */
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new InvalidPasswordException();
        }

        /** 입력한 새 비밀번호를 인코딩 후 return */
        return passwordEncoder.encode(request.getNewPassword());
    }

    public static void validatePassword(Member member, String password, PasswordEncoder passwordEncoder) {

        /** DB에 저장된 비밀번호와 입력한 비밀번호가 일치하지 않을경우 예외 발생 */
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

}
