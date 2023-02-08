package com.portfolio.service;

import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.exception.custom.DuplicateMemberException;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.auth.MemberEditRequest;
import com.portfolio.request.auth.MemberJoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.domain.util.MemberEditor.*;
import static com.portfolio.request.auth.MemberJoinRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final MemberUtil memberUtil;


    @Transactional
    public void join(MemberJoinRequest joinRequest) {
        //중복처리
        if (!memberRepository.existsByUsername(joinRequest.getUsername())) {
            memberRepository.save(toMember(joinRequest, passwordEncoder));
        } else {
            throw new DuplicateMemberException();
        }
    }

    @Transactional
    public void edit(MemberEditRequest editRequest) {
        Member member = memberUtil.getContextMember();
        editMember(editRequest, member, passwordEncoder);
    }

//    public MemberPostResponse findPost(String username, int page) {
//        Member member = memberUtil.getMember(username);
//
//    }

    @Transactional
    public void delete() {
        Member member = memberUtil.getContextMember();
        memberRepository.delete(member);
    }



}
