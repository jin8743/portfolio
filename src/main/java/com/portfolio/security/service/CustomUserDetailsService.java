package com.portfolio.security.config.auth;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.InvalidLoginRequestException;
import com.portfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUser loadUserByUsername(String usernameOrPassword) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findByUsername(usernameOrPassword);

        if (optionalMember.isEmpty()) {
            optionalMember = memberRepository.findByEmail(usernameOrPassword);
        }

        if (optionalMember.isEmpty()) {
            throw new InvalidLoginRequestException();
        }
        Member member = optionalMember.get();

        return new CustomUser(member, List.of(new SimpleGrantedAuthority(member.getRole().toString())));
    }

}
