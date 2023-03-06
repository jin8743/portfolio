package com.portfolio.service;

import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.PasswordChangeRequest;
import com.portfolio.request.member.SignUpRequest;
import com.portfolio.request.member.UnregisterRequest;
import com.portfolio.response.member.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.domain.editor.MemberEditor.editPassword;
import static com.portfolio.exception.custom.CustomBadRequestException.*;
import static com.portfolio.request.member.SignUpRequest.*;
import static org.springframework.security.core.context.SecurityContextHolder.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final MemberUtil memberUtil;
    private final ModelMapper modelMapper;

    /**
     * 회원가입
     */
    @Transactional
    public void saveNewMember(SignUpRequest request) {
        request.setPassword(encodePassword(request.getPassword()));
        memberRepository.save(createNewMember(request));
    }


    /**
     * 비밀번호 수정
     */
    @Transactional
    public void updatePassword(PasswordChangeRequest request) {
        Member member = memberUtil.getContextMember();
        editPassword(member, encodedNewPassword(request, member));
    }

    private String encodedNewPassword(PasswordChangeRequest request, Member member) {
        validatePassword(member, request.getCurrentPassword());
        return encoder.encode(request.getNewPassword());
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void softDelete(UnregisterRequest request) {
        Member member = memberUtil.getContextMember();
        validatePassword(member, request.getPassword());
        memberRepository.delete(member);
        clearContext();
    }

    /**
     * DB에 저장된 member 의 비밀번호와 입력된 비밀번호가 일치하지 않을경우 예외 발생
     */
    private void validatePassword(Member member, String currentPassword) {
        if (encoder.matches(currentPassword, member.getPassword()) == false) {
            throw new CustomBadRequestException(INVALID_PASSWORD);
        }
    }

    private String encodePassword(String password) {
        return encoder.encode(password);
    }

    public MyProfileResponse loadMyProfile() {
        return new MyProfileResponse(memberUtil.getContextMember());
    }
}
