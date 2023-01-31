package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import com.portfolio.repository.MemberRepository;
import com.portfolio.request.auth.LoginRequest;
import com.portfolio.request.auth.MemberJoinRequest;
import com.portfolio.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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

    @Autowired
    MemberUtil memberUtil;

    @BeforeEach
    void clear() {
        memberRepository.deleteAll();
    }

    @DisplayName("/join 요청시 DB에 값이 저장된다")
    @Test
    void test1() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username("username")
                .password("pasword1234")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

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
        System.out.println(member.getRole().toString());
    }

    @DisplayName("/join 요청시 username이 youngjin인 경우 ADMIN권한을 갖는다")
    @Test
    void test2() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username("youngjin")
                .password("password1234")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

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

    @DisplayName("/join 요청시 username과 password는 필수다")
    @Test
    void test3() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username(null)
                .password(null)
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);


        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디를 입력해주세요"))
                .andExpect(jsonPath("$.validation.password").value("비밀번호를 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("/join 요청시 username은 3글자 이상, password는 10글자 이상으로 입력해야한다")
    @Test
    void test5() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username("1")
                .password("1")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }

    @DisplayName("/join 요청시 username은 20글자 이하, password는 20글자 이하로 입력해야한다")
    @Test
    void test6() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .password("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }

    @DisplayName("/join 요청시 username과 password는 영어와 숫자만 입력해야한다")
    @Test
    void test7() throws Exception {
        //given
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username("1!2@3#4$5%abcㄱㄴㄷ")
                .password("1!2@3#4$5%abcㄱㄴㄷ")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요."))
                .andDo(print());

        assertEquals(0L, memberRepository.count());
    }
    @DisplayName("username과 password에 공백이 있으면 안된다")
    @Test
    void test8() throws Exception {
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username(" user  name ")
                .password("비밀번호     123456")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.username").value("아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요."))
                .andExpect(jsonPath("$.validation.password").value("비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요."))
                .andDo(print());
    }

    @DisplayName("/join 요청시 username은 중복되면 안된다")
    @Test
    void test9() throws Exception {
        //given
        memberService.join(MemberJoinRequest.builder()
                .username("username")
                .password("password1234")
                .build());

        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .username("username")
                .password("password123456")
                .build();

        String json = objectMapper.writeValueAsString(memberJoinRequest);

        //then
        mockMvc.perform(post("/join")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디 입니다"))
                .andDo(print());

        assertEquals(1L, memberRepository.count());
    }



    @DisplayName("login 요청시 jwt토큰이 정상적으로 발급된다")
    @Test
    void test10() throws Exception {

        memberService.join(MemberJoinRequest.builder()
                .username("username")
                .password("password1234")
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .username("username")
                .password("password1234")
                .build();

        String json = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(print());

        Member member = memberUtil.getContextMember();
        System.out.println(member);

    }
}