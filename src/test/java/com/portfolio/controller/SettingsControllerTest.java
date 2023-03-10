package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.MemberFactory;
import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.ChangePassword;
import com.portfolio.request.member.SignUp;
import com.portfolio.request.member.Unregister;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired
    private MemberFactory memberFactory;

    @AfterEach
    void clear() {
        memberRepository.deleteAll();
    }

    /** ???????????? ?????? */
    @DisplayName("???????????? ?????? ??????")
    @Test
    void test1() throws Exception {
        //given
        memberFactory.createMember("usernameQ");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword("newpassword12!")
                .newPasswordConfirm("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("usernameQ").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        Member member = memberRepository.findByUsername("usernameQ").get();
        passwordEncoder.matches("newpassword12!", member.getPassword());
    }

    @DisplayName("???????????? ?????? ????????? RequestBody ??? ????????? ?????????")
    @Test
    void test2() throws Exception {
        //given
        memberFactory.createMember("usernameA");

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("usernameA").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("????????? ????????? ????????? ????????? ?????? ????????????"))
                .andDo(print());

        Member member = memberRepository.findByUsername("usernameA").get();
        passwordEncoder.matches("password1234!", member.getPassword());
    }

    @DisplayName("???????????? ?????? ????????? ?????? ???????????? ????????? ?????????")
    @Test
    void test3() throws Exception {
        //given
        memberFactory.createMember("usernameZ");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword(null)
                .newPassword("newpassword12!")
                .newPasswordConfirm("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("usernameZ").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.currentPassword").value("?????? ??????????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ?????? ???????????? ????????? ????????? 2")
    @Test
    void test4() throws Exception {
        //given
        memberFactory.createMember("usernameAS");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .newPassword("newpassword12!")
                .newPasswordConfirm("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("usernameAS").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.currentPassword").value("?????? ??????????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ????????? ?????? ??????????????? DB??? ????????? ??????????????? ??????????????????")
    @Test
    void test5() throws Exception {
        //given
        memberFactory.createMember("usernameQA");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("invalidPassword12!")
                .newPassword("newpassword12!")
                .newPasswordConfirm("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("usernameQA").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("??????????????? ???????????? ????????????."))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ??????????????? ?????????")
    @Test
    void test6() throws Exception {
        //given
        memberFactory.createMember("memberQ");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword(null)
                .newPasswordConfirm("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("memberQ").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.newPassword").value("??? ??????????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ??????????????? ????????? 2")
    @Test
    void test7() throws Exception {
        //given
        memberFactory.createMember("memberB");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPasswordConfirm("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("memberB").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.newPassword").value("??? ??????????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ???????????? ????????? ?????????")
    @Test
    void test8() throws Exception {
        //given
        memberFactory.createMember("memberAB");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword("newpassword12!")
                .newPasswordConfirm(null)
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("memberAB").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.newPasswordConfirm").value("??? ???????????? ????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ???????????? ????????? ????????? 2")
    @Test
    void test9() throws Exception {
        //given
        memberFactory.createMember("memberF");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword("newpassword12!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("memberF").roles("MEMBER"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.newPasswordConfirm").value("??? ???????????? ????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ??????????????? ??? ???????????? ????????? ???????????? ??????")
    @Test
    void test10() throws Exception {
        //given
        memberFactory.createMember("QWER");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword("newPassword1!")
                .newPasswordConfirm("wrong")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("QWER").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("??? ??????????????? ??? ???????????? ????????? ???????????? ????????????."))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ??????????????? ??? ???????????? ????????? ???????????? ?????? 2")
    @Test
    void test11() throws Exception {
        //given
        memberFactory.createMember("QWE");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword(null)
                .newPasswordConfirm(null)
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("QWE").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.newPasswordConfirm").value("??? ???????????? ????????? ??????????????????"))
                .andExpect(jsonPath("$.validation.newPassword").value("??? ??????????????? ??????????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ????????? ??? ??????????????? ????????? ??????????????? ???????????? ?????????")
    @Test
    void test12() throws Exception {
        //given
        memberFactory.createMember("QWE1");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword("password1234!")
                .newPasswordConfirm("password1234!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("QWE1").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("?????? ??????????????? ???????????????."))
                .andDo(print());
    }

    @DisplayName("??????????????? ?????? ????????? ??????????????? ???????????? ??????")
    @Test
    void test13() throws Exception {

        //given
        memberFactory.createMember("QWET");

        //when
        String json = objectMapper.writeValueAsString(ChangePassword.builder()
                .currentPassword("password1234!")
                .newPassword("changePassword1!")
                .newPasswordConfirm("changePassword1!")
                .build());

        //then
        mockMvc.perform(patch("/settings/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    /**
     * ????????????
     */
    @DisplayName("???????????? ????????? ????????? ???????????? ??????")
    @Test
    void test14() throws Exception {
        //given
        memberFactory.createMember("deleteMemberA");

        //when
        String json = objectMapper.writeValueAsString(Unregister.builder()
                .password("password1234!")
                .build());

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("deleteMemberA").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("???????????? ????????? ????????? ??????????????? DB??? ????????? ?????? ??????????????????")
    @Test
    void test15() throws Exception {
        //given
        memberFactory.createMember("deleteMemberB");

        //when
        String json = objectMapper.writeValueAsString(Unregister.builder()
                .password("wrongPassword!")
                .build());

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("deleteMemberB").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("??????????????? ???????????? ????????????."))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("???????????? ????????? ????????? ??????????????? ?????????")
    @Test
    void test16() throws Exception {
        //given
        memberFactory.createMember("deleteMemberC");

        //when
        String json = objectMapper.writeValueAsString(Unregister.builder()
                .password(null)
                .build());

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("deleteMemberC").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.password").value("??????????????? ??????????????????"))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("???????????? ????????? ????????? ??????????????? ????????? 2")
    @Test
    void test17() throws Exception {
        //given
        memberFactory.createMember("deleteMemberD");

        //when
        String json = objectMapper.writeValueAsString(Unregister.builder()
                .build());

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("deleteMemberD").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.password").value("??????????????? ??????????????????"))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("???????????? ????????? RequestBody ??? ????????? ?????????")
    @Test
    void test18() throws Exception {
        //given
        memberFactory.createMember("deleteMemberE");

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .with(user("deleteMemberE").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("????????? ????????? ????????? ????????? ?????? ????????????"))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    /**
     * ??? ?????? ??????
     */
    @DisplayName("??? ?????? ?????? ??????")
    @Test
    void test19() throws Exception {
        //given
        Member member = memberFactory.createMember("QQQ");

        //then
        mockMvc.perform(get("/settings/profile")
                        .with(user("QQQ").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andDo(print());
    }

    @DisplayName("????????? ??????????????? ???????????? ??????")
    @Test
    void test20() throws Exception {
        //given
        Member member = memberFactory.createMember("ZXCg");

        //then
        mockMvc.perform(get("/settings/profile")
                        .with(user("SSS").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ????????? ????????????."));
    }
}
