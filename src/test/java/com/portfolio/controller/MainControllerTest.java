package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Comment;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.Login;
import com.portfolio.request.member.SignUp;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAll();
    }

    /** 로그인 */
    @DisplayName("아이디로 로그인 성공")
    @Test
    void test1() throws Exception {
        //given
        memberService.saveNewMember(SignUp.builder()
                .username("username")
                .email("1234@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .usernameOrEmail("username")
                .password("password!").build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("username"))
                .andDo(print());
    }

    @DisplayName("이메일로 로그인 성공")
    @Test
    void test2() throws Exception {
        //given
        memberService.saveNewMember(SignUp.builder()
                .username("username123asd")
                .email("youngjin8743@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .usernameOrEmail("youngjin8743@naver.com")
                .password("password!").build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("username123asd"))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 회원의 아이디와 비밀번호로 로그인 할수 없다")
    @Test
    void test3() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .usernameOrEmail("1234")
                .password("@@@@@@@").build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @DisplayName("로그인시 아이디 또는 이메일은 필수다")
    @Test
    void test4() throws Exception {
        //given
        memberService.saveNewMember(SignUp.builder()
                .username("username1234")
                .email("12345w@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .usernameOrEmail(null)
                .password("password!").build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @DisplayName("로그인시 아이디 또는 이메일은 필수다 2")
    @Test
    void test5() throws Exception {
        //given
        memberService.saveNewMember(SignUp.builder()
                .username("username1235A")
                .email("123451@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .password("password!").build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @DisplayName("로그인시 비밀번호는 필수다")
    @Test
    void test224() throws Exception {
        //given
        memberService.saveNewMember(SignUp.builder()
                .username("username1236")
                .email("12345@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .usernameOrEmail("12345@naver.com")
                .password(null).build());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @DisplayName("로그인시 비밀번호는 필수다 2")
    @Test
    void test6() throws Exception {
        //given
        memberService.saveNewMember(SignUp.builder()
                .username("username123P")
                .email("12345QWE@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(Login.builder()
                .usernameOrEmail("12345QWE@naver.com")
                .build());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @WithMockUser
    @DisplayName("로그아웃")
    @Test
    void test7() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }
}
