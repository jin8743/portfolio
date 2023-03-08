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

    /** 비밀번호 변경 */
    @DisplayName("비밀번호 변경 요청")
    @Test
    void test9() throws Exception {
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

    @DisplayName("비밀번호 변경 요청시 RequestBody 에 내용은 필수다")
    @Test
    void test92() throws Exception {
        //given
        memberFactory.createMember("usernameA");

        //then
        mockMvc.perform(patch("/settings/password")
                        .with(user("usernameA").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        Member member = memberRepository.findByUsername("usernameA").get();
        passwordEncoder.matches("password1234!", member.getPassword());
    }

    @DisplayName("비밀번호 변경 요청시 기존 비밀번호 입력은 필수다")
    @Test
    void test12() throws Exception {
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
                .andExpect(jsonPath("$.validation.currentPassword").value("기존 비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 기존 비밀번호 입력은 필수다 2")
    @Test
    void test122() throws Exception {
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
                .andExpect(jsonPath("$.validation.currentPassword").value("기존 비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 입력된 기존 비밀번호는 DB에 저장된 비밀번호와 일치해야된다")
    @Test
    void test1222() throws Exception {
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
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호는 필수다")
    @Test
    void test1122() throws Exception {
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
                .andExpect(jsonPath("$.validation.newPassword").value("새 비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호는 필수다 2")
    @Test
    void test112122() throws Exception {
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
                .andExpect(jsonPath("$.validation.newPassword").value("새 비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호 확인은 필수다")
    @Test
    void test11922() throws Exception {
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
                .andExpect(jsonPath("$.validation.newPasswordConfirm").value("새 비밀번호 확인을 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호 확인은 필수다 2")
    @Test
    void test1922() throws Exception {
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
                .andExpect(jsonPath("$.validation.newPasswordConfirm").value("새 비밀번호 확인을 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호와 새 비밀번호 확인은 동일해야 된다")
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
                .andExpect(jsonPath("$.message").value("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다."))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호와 새 비밀번호 확인은 동일해야 된다 2")
    @Test
    void test120() throws Exception {
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
                .andExpect(jsonPath("$.validation.newPasswordConfirm").value("새 비밀번호 확인을 입력해주세요"))
                .andExpect(jsonPath("$.validation.newPassword").value("새 비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("비밀번호 변경 요청시 새 비밀번호는 기존의 비밀번호와 동일하면 안된다")
    @Test
    void test1220() throws Exception {
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
                .andExpect(jsonPath("$.message").value("현재 비밀번호와 동일합니다."))
                .andDo(print());
    }

    @DisplayName("로그인하지 않은 상태로 비밀번호를 변경할수 없다")
    @Test
    void test12220() throws Exception {

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
     * 회원탈퇴
     */
    @DisplayName("회원탈퇴 요청시 계정이 비활성화 된다")
    @Test
    void test26() throws Exception {
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

    @DisplayName("회원탈퇴 요청시 입력된 비밀번호가 DB에 저장된 값과 동일해야한다")
    @Test
    void test22() throws Exception {
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
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("회원탈퇴 요청시 입력된 비밀번호는 필수다")
    @Test
    void test2122() throws Exception {
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
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요"))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("회원탈퇴 요청시 입력된 비밀번호는 필수다 2")
    @Test
    void test21902() throws Exception {
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
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요"))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
        assertEquals(1L, memberRepository.count());
    }

    @DisplayName("회원탈퇴 요청시 RequestBody 에 내용은 필수다")
    @Test
    void test221902() throws Exception {

        //given
        memberFactory.createMember("deleteMemberE");

        //then
        mockMvc.perform(delete("/settings/unregister")
                        .with(user("deleteMemberE").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("탈퇴한 회원의 아이디로 회원가입할수 없다")
    @Test
    void test3() throws Exception {
        //given
        Member member = Member.builder()
                .username("deletedMember123")
                .email("1234B@naver.com")
                .password("password1234!")
                .build();
        memberRepository.save(member);
        memberRepository.delete(member);

        //when
        String json = objectMapper.writeValueAsString(SignUp.builder()
                .username("deletedMember123")
                .email("newMember@naver.com")
                .password("pwd12345!!")
                .passwordConfirm("pwd12345!!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 사용중이거나 탈퇴한 아이디입니다."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("탈퇴한 회원의 이메일로 회원가입할수 없다")
    @Test
    void test32() throws Exception {
        //given
        Member member = Member.builder()
                .username("usernameQWE")
                .email("1234A@naver.com")
                .password("password1234!")
                .build();
        memberRepository.save(member);
        memberRepository.delete(member);

        //when
        String json = objectMapper.writeValueAsString(SignUp.builder()
                .username("newUser")
                .email("1234A@naver.com")
                .password("pwd12345!!")
                .passwordConfirm("pwd12345!!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 사용중이거나 탈퇴한 이메일입니다."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }
}
