package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.PasswordChangeRequest;
import com.portfolio.request.member.SignUpRequest;
import com.portfolio.request.member.UnregisterRequest;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SettingsControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void clear() {
        memberRepository.deleteAll();
    }

    @DisplayName("비밀번호 변경 요청시 DB에 값이 반영된다")
    @Test
    void test9() throws Exception {
        //given
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username1")
                .email("1234@naver.com")
                .password("password1234")
                .passwordConfirm("password1234")
                .build());

        String json = objectMapper.writeValueAsString(PasswordChangeRequest.builder()
                .currentPassword("password1234")
                .newPassword("newpassword1234!")
                .newPasswordConfirm("newpassword1234!")
                .build());


        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("username1").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());


        Member member = memberRepository.findByUsername("username1").get();
        passwordEncoder.matches("12345678", member.getPassword());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호와 새 비밀번호 확인은 동일해야 된다")
    @Test
    void test10() throws Exception {
        //given
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username2")
                .email("qwer@na.com")
                .password("password1234")
                .passwordConfirm("password1234")
                .build());

        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .currentPassword("password1234")
                .newPassword("12345678")
                .newPasswordConfirm("abcd")
                .build();

        String json = objectMapper.writeValueAsString(request);


        //then
        mockMvc.perform(patch("/settings/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("username2").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("잘못된 요청입니다"))
                .andDo(print());

        Member member = memberRepository.findByUsername("username2").get();
        passwordEncoder.matches("password1234", member.getPassword());
    }

    @DisplayName("회원탈퇴 요청시 Soft Delete 처리된다")
    @Test
    void test26() throws Exception {
        //given
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username4")
                .email("qwer@gmail.com")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());

        String json = objectMapper.writeValueAsString(UnregisterRequest.builder()
                .password("password1234!")
                .build());

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("username4").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }

    @DisplayName("탈퇴한 member 의 username 과 email 로 회원가입 할수없다")
    @Test
    void test3() throws Exception {
        //given
        Member member = Member.builder()
                .username("username")
                .email("1234@naver.com")
                .password("password1234!")
                .build();
        memberRepository.save(member);
        memberRepository.delete(member);


        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("username")
                .email("1@naver.com")
                .password("pwd12345!!")
                .passwordConfirm("pwd12345!!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }
}
