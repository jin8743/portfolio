package com.portfolio.security.service;

import com.portfolio.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUser extends User {

    private Member member;

    public CustomUser(Member member) {
        super(member.getUsername(), member.getPassword(), member.getIsEnabled(),
                member.getIsEnabled(), true, true,
                List.of(new SimpleGrantedAuthority(member.getRole().toString())));
        this.member = member;
    }
}
