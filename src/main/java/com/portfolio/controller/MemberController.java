package com.portfolio.controller;

import com.portfolio.request.auth.MemberEditRequest;
import com.portfolio.request.auth.MemberJoinRequest;
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
    public void join(@RequestBody @Validated MemberJoinRequest memberJoinRequest) {
        memberService.join(memberJoinRequest);
    }

//    @GetMapping("/profile/{username}")
//    public MemberPostResponse get(@PathVariable String username,
//                                  @RequestParam(required = false) Integer page) {
//
//        memberService.findPost(username, page);
//    }


    @PatchMapping("/myPage/edit")
    public void update(@RequestBody MemberEditRequest editRequest) {
        memberService.edit(editRequest);
    }

    @DeleteMapping("/myPage/unregister")
    public void unregister() {
        memberService.delete();
    }
}
