package com.portfolio.service;

import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.member.PasswordChangeRequest;
import com.portfolio.request.member.JoinRequest;
import com.portfolio.request.member.UnregisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.domain.editor.member.MemberEditor.*;
import static com.portfolio.request.member.JoinRequest.*;
import static com.portfolio.request.member.PasswordChangeRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberUtil memberUtil;

    /** 회원가입 */
    @Transactional
    public void join(JoinRequest joinRequest) {
        //비밀번호와 비밀번호 확인이 일치하는지 확인
        validate(joinRequest);

        //member 중복여부 확인
        memberUtil.memberExists(joinRequest.getUsername());

        Member member = toMember(joinRequest, passwordEncoder);
        memberRepository.save(member);
    }

    /** 비밀번호 수정 */
    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        //비밀번호 검증
        validatePassword(request);

        Member member = memberUtil.getContextMember();
        editPassword(request, member, passwordEncoder);
    }

    /** 회원 탈퇴 */
    @Transactional
    public void disable(UnregisterRequest request) {
        Member member = memberUtil.getContextMember();
        unregister(request, member, passwordEncoder);
    }
}
