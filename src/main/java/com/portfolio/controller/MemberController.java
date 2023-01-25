package com.portfolio.controller;

import com.portfolio.request.LoginRequest;
import com.portfolio.request.MemberJoinRequest;
import com.portfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public void join(@RequestBody @Validated MemberJoinRequest memberJoinRequest) {
        memberService.join(memberJoinRequest);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/auth")
    public void auth(Authentication authentication) {
        System.out.println(authentication.getPrincipal());

    }
}
