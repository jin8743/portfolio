package com.portfolio.controller;

import com.portfolio.request.member.PasswordChangeRequest;
import com.portfolio.request.member.JoinRequest;
import com.portfolio.request.member.UnregisterRequest;
import com.portfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public void join(@RequestBody @Validated JoinRequest joinRequest) {
        memberService.join(joinRequest);
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/myInfo/changePasswd")
    public void update(@RequestBody PasswordChangeRequest editRequest) {
        memberService.changePassword(editRequest);
    }

    /**
     * 회원 탈퇴
     */
    @PatchMapping("/myInfo/unregister")
    public void unregister(@RequestBody UnregisterRequest request) {
        memberService.disable(request);
    }


    /** 비공계 계정으로 전환 (작성글, 작성댓글 전부 비공개 처리) */
    @PatchMapping("/myInfo/private")
    public void
}
