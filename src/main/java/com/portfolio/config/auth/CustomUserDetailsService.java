package com.portfolio.config.auth;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.InvalidLoginRequestException;
import com.portfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(InvalidLoginRequestException::new);

        return new CustomUserDetails(member);
    }
}
