package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.LoginRequest;
import com.portfolio.request.member.SignUpRequest;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.portfolio.domain.MemberRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MemberUtil memberUtil;
    @Autowired
    PasswordEncoder encoder;
    @AfterEach
    void clear() {
        memberRepository.deleteAll();
    }

    /**
      *  회원가입 요청 Controller 테스트
     *   아이디, 비밀번호 정규 표현식 검증 테스트는
     *   test.service.UsernamePasswordRegexTest 에서 진행
     */

    @DisplayName("회원가입 요청 정상처리")
    @Test
    void test1() throws Exception {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .username("username1")
                .email("12345@naver.com")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(1L, memberRepository.countActiveMember());
        Member member = memberRepository.findAllActiveMember().get(0);

        assertEquals(request.getUsername(), member.getUsername());
        assertTrue(encoder.matches(request.getPassword(), member.getPassword()));
        assertEquals(request.getEmail(), member.getEmail());
        assertEquals(ROLE_MEMBER, member.getRole());
        assertTrue(member.getIsEnabled());
    }

    @DisplayName("회원가입 요청시 필수항목들을 입력해야된다")
    @Test
    void test2() throws Exception {
        mockMvc.perform(post("/join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());
    }

    @DisplayName("아이디는 필수다")
    @Test
    void test3() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username(null)
                .email("123456@naver.com")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());
        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디를 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("아이디는 필수다 2")
    @Test
    void test4() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .email("14@naver.com")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());
        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디를 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("이메일은 필수다")
    @Test
    void test5() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email(null)
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());
        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.email").value("이메일을 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("이메일은 필수다 2")
    @Test
    void test6() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());

        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.email").value("이메일을 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }
    @DisplayName("이메일 형식으로 입력해야된다")
    @Test
    void test7() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("12345678")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());

        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation.email").value("이메일 형식으로 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호는 필수다")
    @Test
    void test8() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .password(null)
                .passwordConfirm("password1234!")
                .build());
        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호는 필수다 2")
    @Test
    void test9() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .passwordConfirm("password1234!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호 확인은 필수다")
    @Test
    void test10() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .password("password1234!")
                .passwordConfirm(null)
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호 확인은 필수다 2")
    @Test
    void test11() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .password("password1234!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호와 비밀먼호 확인은 일치해야 한다")
    @Test
    void test12() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .password("password1234!")
                .passwordConfirm("1251223")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호와 비밀번호 확인이 일치하지 않습니다."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호와 비밀먼호 확인은 일치해야 한다 2")
    @Test
    void test13() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .password(null)
                .passwordConfirm(null)
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("비밀번호와 비밀먼호 확인은 일치해야 한다 3")
    @Test
    void test14() throws Exception {
        //given
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("jn1234")
                .email("1234@naver.com")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호를 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.countActiveMember());
    }

    @DisplayName("회원가입 요청시 이미 사용중인 아이디로 등록할수 없다")
    @Test
    void test15() throws Exception {
        //given
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username2")
                .email("1234@naver.com")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());

        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("username2")
                .email("qwe@naver.com")
                .password("password123456!")
                .passwordConfirm("password123456!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("이미 사용중이거나 탈퇴한 아이디입니다."))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }

    @DisplayName("회원가입 요청시 이미 사용중인 이메일로 등록할수 없다")
    @Test
    void test16() throws Exception {
        //given
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username123")
                .email("email@naver.com")
                .password("password1234!")
                .passwordConfirm("password1234!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(SignUpRequest.builder()
                .username("abcd")
                .email("email@naver.com")
                .password("password123456!")
                .passwordConfirm("password123456!")
                .build());

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("이미 사용중이거나 탈퇴한 이메일입니다."))
                .andDo(print());

        assertEquals(1L, memberRepository.countActiveMember());
    }
}

