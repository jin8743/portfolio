package com.portfolio.service;

import com.portfolio.exception.DuplicateMemberException;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.Login;
import com.portfolio.request.MemberJoin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberJoin memberJoin) {
        //중복처리
        if (memberRepository.findByUsername(memberJoin.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException();
        }

        memberRepository.save(MemberJoin.toMember(memberJoin, passwordEncoder));
    }

    @Transactional
    public Long login(Login login) {
        memberRepository.findByUsernameAndPassword(login.getUsername(), login.getPassword())
                .orElseThrow();
        return null;
    }
}
