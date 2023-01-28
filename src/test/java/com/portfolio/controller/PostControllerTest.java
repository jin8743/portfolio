package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.PostRepository;
import com.portfolio.request.LoginRequest;
import com.portfolio.request.MemberJoinRequest;
import com.portfolio.request.PostCreateRequest;
import com.portfolio.service.MemberService;
import com.portfolio.service.PostService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostControllerTest {

    private static final String AUTH = "Authorization";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    private String jwt;


    @BeforeEach
    void clear() {
        postRepository.deleteAll();
    }

    @BeforeAll
    void createToken() throws Exception {

        memberService.join(MemberJoinRequest.builder()
                .username("username")
                .password("password1234")
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .username("username")
                .password("password1234")
                .build();

        String json = objectMapper.writeValueAsString(loginRequest);

        MvcResult mvcResult = mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andReturn();

        jwt = mvcResult.getResponse().getHeader(AUTH);
    }

    @DisplayName("/posts 요청시 DB에 값이 저장된다")
    @Test
    void test1() throws Exception {

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(postCreateRequest);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTH, jwt))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);

        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
    }

    @DisplayName("/posts 요청시 title과 content 값은 필수다")
    @Test
    void test2() throws Exception {

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(null)
                .content(null)
                .build();

        String json = objectMapper.writeValueAsString(postCreateRequest);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .header(AUTH, jwt))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());
    }
}
