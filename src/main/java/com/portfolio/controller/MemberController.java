package com.portfolio.controller;

import com.portfolio.request.Login;
import com.portfolio.request.MemberJoin;
import com.portfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public void join(@RequestBody @Validated MemberJoin memberJoin) {
        memberService.join(memberJoin);
    }

    @PostMapping("/login")
    public void login(@RequestBody Login login) {
        Long memberId = memberService.login(login);

    }
}
