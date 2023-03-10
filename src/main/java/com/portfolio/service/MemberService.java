package com.portfolio.service;

import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.common.Page;
import com.portfolio.request.member.ChangePassword;
import com.portfolio.request.member.SignUp;
import com.portfolio.response.member.MemberProfileForAdminResponse;
import com.portfolio.response.member.MemberBasicProfileResponse;
import com.portfolio.response.member.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.editor.MemberEditor.editPassword;
import static com.portfolio.exception.custom.CustomNotFoundException.*;
import static com.portfolio.request.member.SignUp.*;
import static org.springframework.security.core.context.SecurityContextHolder.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

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

    //관리자 권한으로 강퇴
    @Transactional
    public void unregisterByAdmin(String username) {
        Member member = memberRepository.findActiveMemberByUsername(username);
        memberRepository.delete(member);
    }

    /**
     *  내 회원 정보 조회
     */
    public MyProfileResponse loadMyProfile() {
        return new MyProfileResponse(memberUtil.getContextMember());
    }

    /**
     * 특정 회원 기본 정보 조회
     * 탈퇴 회원인 경우 회원이름 "탈퇴 회원" 으로 표시
     */
    public MemberBasicProfileResponse loadMemberBasicProfile(String username) {
        return getMemberProfile(username);
    }

    private MemberBasicProfileResponse getMemberProfile(String username) {
        Member member = memberUtil.getMember(username);
        Long postCount = postRepository.findPostCountByMember(member);
        Long commentCount = commentRepository.findCommentCountByMember(member);
        return MemberBasicProfileResponse.builder()
                .member(member)
                .totalPosts(postCount)
                .totalComments(commentCount).build();
    }

    /**
     * 회원정보 페이징 조회
     * 관리자만 조회 가능
     */
    public List<MemberProfileForAdminResponse> loadMemberListForAdmin(Page page) {
        List<Member> list = memberRepository.findMemberList(page.getPage());

        return list.isEmpty() ? new ArrayList<>() : list.stream()
                .map(MemberProfileForAdminResponse::new).collect(Collectors.toList());
    }

    /**
     * 회원정보 단건 조회
     * 관리자만 조회 가능
     */
    public MemberProfileForAdminResponse loadMemberForAdmin(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomNotFoundException(MEMBER_NOT_FOUND));

        return new MemberProfileForAdminResponse(member);
    }
}
