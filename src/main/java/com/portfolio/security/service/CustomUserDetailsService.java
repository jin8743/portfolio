package com.portfolio.security.service;

import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrPassword) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findByUsername(usernameOrPassword);

        if (optionalMember.isEmpty()) {
            optionalMember = memberRepository.findByEmail(usernameOrPassword);
        }
        if (optionalMember.isEmpty()) {
            throw new UsernameNotFoundException(INVALID_LOGIN_INFO);
        }
        Member member = optionalMember.get();

        if (member.getIsEnabled() == false) {
            throw new DisabledException(UNREGISTERED_ACCOUNT);
        }
        return new CustomUser(member);
    }

}
