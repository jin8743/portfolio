package com.portfolio.controller;

import com.portfolio.request.member.SignUp;
import com.portfolio.request.validator.member.SignUpValidator;
import com.portfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final SignUpValidator signUpValidator;

    @InitBinder("signUp")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpValidator);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public void signUp(@RequestBody @Validated SignUp signUp) {
        memberService.saveNewMember(signUp);
    }
}
