package com.portfolio.service;

import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.ChangePassword;
import com.portfolio.request.member.SignUp;
import com.portfolio.response.member.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.domain.editor.MemberEditor.editPassword;
import static com.portfolio.request.member.SignUp.*;
import static org.springframework.security.core.context.SecurityContextHolder.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final MemberUtil memberUtil;

    /**
     * 회원가입
     */
    @Transactional
    public void saveNewMember(SignUp request) {
        request.setPassword(encoder.encode(request.getPassword()));
        memberRepository.save(createNewMember(request));
    }

    /**
     * 비밀번호 수정
     */
    @Transactional
    public void updatePassword(ChangePassword request) {
        Member member = memberUtil.getContextMember();
        String newEncodedPassword = encoder.encode(request.getNewPassword());
        editPassword(member, newEncodedPassword);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void unregister() {
        memberRepository.delete(memberUtil.getContextMember());
        clearContext();
    }

    /**
     *  내 회원 정보 조회
     */
    public MyProfileResponse loadMyProfile() {
        return new MyProfileResponse(memberUtil.getContextMember());
    }
}
