package com.portfolio.controller;

import com.portfolio.request.auth.MemberEditRequest;
import com.portfolio.request.auth.MemberJoinRequest;
import com.portfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
    public void join(@RequestBody @Validated MemberJoinRequest memberJoinRequest) {
        memberService.join(memberJoinRequest);
    }

//    @GetMapping("/{username}")
//    public MemberResponse get(@PathVariable String username) {
//        memberService.find(username);
//    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/auth")
    public void auth(Authentication authentication) {
        System.out.println(authentication.getPrincipal());
        Object memberId = authentication.getCredentials();
        System.out.println(memberId);
    }

    @PatchMapping("/myPage/edit")
    public void update(@RequestBody MemberEditRequest editRequest) {
        memberService.edit(editRequest);
    }

    @DeleteMapping("/myPage/unregister")
    public void unregister() {
        memberService.delete();
    }
}
