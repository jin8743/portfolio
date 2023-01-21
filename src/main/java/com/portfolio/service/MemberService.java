package com.portfolio.service;

import com.portfolio.exception.custom.DuplicateMemberException;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.MemberJoinRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Transactional
    public void join(MemberJoinRequest memberJoinRequest) {
        //중복처리
        if (memberRepository.findByUsername(memberJoinRequest.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException();
        }

        memberRepository.save(MemberJoinRequest.toMember(memberJoinRequest, passwordEncoder));
    }
}
