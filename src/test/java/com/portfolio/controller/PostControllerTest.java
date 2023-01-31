package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Post;
import com.portfolio.repository.MemberRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.auth.MemberJoinRequest;
import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.service.MemberService;
import com.portfolio.service.PostService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberUtil memberUtil;

    @BeforeEach
    void clear() {
        postRepository.deleteAll();
    }

    @BeforeEach
    void insertMember() throws Exception {

        memberService.join(MemberJoinRequest.builder()
                .username("username")
                .password("password1234")
                .build());
    }

    @DisplayName("posts 요청시 DB에 값이 저장된다")
    @Test
    void test1() throws Exception {

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();


        String json = objectMapper.writeValueAsString(postCreateRequest);

        mockMvc.perform(post("/posts")
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);

        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
    }

    @DisplayName("posts 요청시 title과 content 값은 필수다")
    @Test
    void test2() throws Exception {

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(null)
                .content(null)
                .build();

        String json = objectMapper.writeValueAsString(postCreateRequest);

        mockMvc.perform(post("/posts")
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("글 단건 조회")
    @Test
    void test3() throws Exception {

//        //given
//        Member member = memberRepository.findByUsername("username").orElseThrow(MemberNotFoundException::new);
//
//        Post post = Post.builder()
//                .title("제목입니다")
//                .content("내용입니다")
//                .member(member)
//                .build();
//
//        postRepository.save(post);
//
//        PostResponse postResponse = postService.get(post.getId());
//
//        //when
//        mockMvc.perform(get("/posts/{postId}", post.getId())
//                        .with(jwt().jwt(jwt -> jwt.subject("username")))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value())
//
////                .header(AUTH, jwt));
//
//        //then

    }
}
