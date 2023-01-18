package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.MemberJoin;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void clear() {
        memberRepository.deleteAll();
    }

    @DisplayName("/join 요청시 DB에 값이 저장된다")
    @Test
    void test1() throws Exception {
        //given
        MemberJoin memberJoin = MemberJoin.builder()
                .username("username")
                .password("pasword1234")
                .build();

        String json = objectMapper.writeValueAsString(memberJoin);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());


        assertEquals(1L, memberRepository.count());
        assertNotEquals(2L, memberRepository.count());

        Member member = memberRepository.findAll().get(0);
        assertEquals("username", member.getUsername());
        assertEquals(MemberRole.ROLE_MEMBER, member.getRole());
    }

    @DisplayName("/join 요청시 username이 youngjin인 경우 ADMIN권한을 갖는다")
    @Test
    void test2() throws Exception {
        //given
        MemberJoin memberJoin = MemberJoin.builder()
                .username("youngjin")
                .password("password1234")
                .build();

        String json = objectMapper.writeValueAsString(memberJoin);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());


        assertEquals(1L, memberRepository.count());

        Member member = memberRepository.findAll().get(0);
        assertEquals(MemberRole.ROLE_ADMIN, member.getRole());
    }

    @DisplayName("/join 요청시 username은 필수다")
    @Test
    void test3() throws Exception {
        //given
        MemberJoin memberJoin = MemberJoin.builder()
                .username(null)
                .password("password1234").build();

        String json = objectMapper.writeValueAsString(memberJoin);


        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("/join 요청시 password는 필수다")
    @Test
    void test4() throws Exception {
        //given
        MemberJoin memberJoin = MemberJoin.builder()
                .username("username")
                .password(null).build();

        String json = objectMapper.writeValueAsString(memberJoin);


        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("/join 요청시 username은 3글자 이상, 20글자 이하로 입력해야한다")
    @Test
    void test5() throws Exception {
        //given
        MemberJoin memberJoin = MemberJoin.builder()
                .username("1")
                .password("pasword1234")
                .build();

        String json = objectMapper.writeValueAsString(memberJoin);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디는 8글자 이상 20글자 이상으로 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }

    @DisplayName("/join 요청시 password는 10글자 이상, 20글자 이하로 입력해야한다")
    @Test
    void test6() throws Exception {
        //given
        MemberJoin memberJoin = MemberJoin.builder()
                .username("username")
                .password("1")
                .build();

        String json = objectMapper.writeValueAsString(memberJoin);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 10글자 이상 20글자 이상으로 입력해주세요"))
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }

    @DisplayName("/join 요청시 username은 중복되면 안된다")
    @Test
    void test7() throws Exception {
        //given
        memberService.join(MemberJoin.builder()
                .username("username")
                .password("password1234")
                .build());

        MemberJoin memberJoin = MemberJoin.builder()
                .username("username")
                .password("비밀번호123456")
                .build();

        String json = objectMapper.writeValueAsString(memberJoin);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디 입니다"))
                .andDo(print());

        assertEquals(1L, memberRepository.count());
    }
}