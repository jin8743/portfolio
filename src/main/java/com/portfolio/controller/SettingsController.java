package com.portfolio.controller;

import com.portfolio.request.member.PasswordChangeRequest;
import com.portfolio.request.member.UnregisterRequest;
import com.portfolio.request.validator.member.PasswordChangeValidator;
import com.portfolio.response.member.MyProfileResponse;
import com.portfolio.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static com.portfolio.controller.SettingsController.ROOT;
import static com.portfolio.controller.SettingsController.SETTINGS;

@RestController
@RequestMapping(ROOT + SETTINGS)

@RequiredArgsConstructor
public class SettingsController {

    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PASSWORD = "/password";
    static final String PROFILE = "/profile";
    static final String UNREGISTER = "/unregister";
    private final MemberService memberService;


    @InitBinder("passwordChangeRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordChangeValidator());
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping(PASSWORD)
    public void updatePassword(@RequestBody @Validated PasswordChangeRequest request) {
        memberService.updatePassword(request);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping(UNREGISTER)
    public void unregister(@RequestBody UnregisterRequest request) {
        memberService.softDelete(request);
    }

    /**
     * 내 정보 조회
     */
    @GetMapping(PROFILE)
    public MyProfileResponse loadMyProfile() {
        return memberService.loadMyProfile();
    }

}
