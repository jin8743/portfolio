package com.portfolio.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Comment;
import com.portfolio.domain.Post;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.LoginRequest;
import com.portfolio.request.member.SignUpRequest;
import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.portfolio.request.post.PostCreateRequest.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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

    @DisplayName("아이디로 로그인 성공")
    @Test
    void test1() throws Exception {
        //given
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username")
                .email("1234@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(LoginRequest.builder()
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
        memberService.saveNewMember(SignUpRequest.builder()
                .username("username123")
                .email("12345@naver.com")
                .password("password!")
                .passwordConfirm("password!")
                .build());

        //when
        String json = objectMapper.writeValueAsString(LoginRequest.builder()
                .usernameOrEmail("12345@naver.com")
                .password("password!").build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("username123"))
                .andDo(print());

    }

    @DisplayName("로그인 실패")
    @Test
    void test3() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(LoginRequest.builder()
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

    @DisplayName("로그인 실패 2")
    @Test
    void test4() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(LoginRequest.builder()
                .usernameOrEmail(null)
                .password(null).build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @DisplayName("로그인 실패 2")
    @Test
    void test5() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(Comment.builder()
                .content("1234")
                .member(null)
                .post(null)
                .build());

        //then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());
    }

    @DisplayName("로그인 실패 3")
    @Test
    void test6() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
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
