package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Board;
import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.exception.custom.MemberNotFoundException;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Post;
import com.portfolio.repository.MemberRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.auth.MemberJoinRequest;
import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.request.post.PostEditRequest;
import com.portfolio.request.post.PostSearchRequest;
import com.portfolio.response.post.SinglePostResponse;
import com.portfolio.service.MemberService;
import com.portfolio.service.PostService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void clear() {
        postRepository.deleteAll();
    }

    @BeforeEach
    void insertMember() throws Exception {
        memberRepository.deleteAll();

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

        //given
        Member member = memberRepository.findByUsername("username").orElseThrow(MemberNotFoundException::new);


        Board board = Board.builder()
                .boardName("free")
                .build();

        boardRepository.save(board);

        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .member(member)
                .board(board)
                .build();

        postRepository.save(post);


        List<Comment> comments = IntStream.range(1, 11).mapToObj(
                i -> Comment.builder()
                        .post(post)
                        .content("내용 " + i)
                        .member(member)
                        .build()
        ).collect(Collectors.toList());

        commentRepository.saveAll(comments);

        System.out.println("============================================");
        //when
        mockMvc.perform(get("/board/view?id=free&no=1")
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());


        //then

    }

    @DisplayName("특정 게시판 페이징 조회")
    @Test
    void test6() throws Exception {

        //given
        Member member = memberRepository.findByUsername("username").orElseThrow(MemberNotFoundException::new);

        Board board = Board.builder()
                .boardName("free")
                .build();

        boardRepository.save(board);

        IntStream.rangeClosed(1, 50).forEach(i -> {
            Post post = Post.builder()
                    .board(board)
                    .member(member)
                    .title("제목입니다 " + i)
                    .content("내용입니다 " + i)
                    .build();
            postRepository.save(post);

            List<Comment> comments = IntStream.rangeClosed(1, i).mapToObj(
                    o -> Comment.builder()
                            .post(post)
                            .content(i + " 번쨰 글 댓글 " + o)
                            .member(member)
                            .build()
            ).collect(Collectors.toList());

            commentRepository.saveAll(comments);

        });
        System.out.println("==============================================================");
        //then
        mockMvc.perform(get("/board/lists?id=free&page=    &list_num=20")
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 단건 조회")
    @Test
    void test5() throws Exception {

        //given
        Member member = memberRepository.findByUsername("username").orElseThrow(MemberNotFoundException::new);

        Board board = Board.builder()
                .boardName("free")
                .build();

        boardRepository.save(board);

        IntStream.rangeClosed(1, 50).forEach(i -> {
            Post post = Post.builder()
                    .board(board)
                    .member(member)
                    .title("제목입니다 " + i)
                    .content("내용입니다 " + i)
                    .build();
            postRepository.save(post);

            List<Comment> comments = IntStream.rangeClosed(1, i).mapToObj(
                    o -> Comment.builder()
                            .post(post)
                            .content(i + " 번쨰 글 댓글 " + o)
                            .member(member)
                            .build()
            ).collect(Collectors.toList());

            commentRepository.saveAll(comments);

        });

        System.out.println("============================");
        //then
        mockMvc.perform(get("/board/lists?id=free&list_num=a")
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 멤버 작성글 페이징 조회")
    @Test
    void test8() throws Exception {

        //given
        Member member = memberRepository.findByUsername("username").orElseThrow(MemberNotFoundException::new);

        Board board = Board.builder()
                .boardName("free")
                .build();

        boardRepository.save(board);

        IntStream.rangeClosed(1, 50).forEach(i -> {
            Post post = Post.builder()
                    .board(board)
                    .member(member)
                    .title("제목입니다 " + i)
                    .content("내용입니다 " + i)
                    .build();
            postRepository.save(post);

            List<Comment> comments = IntStream.rangeClosed(1, i).mapToObj(
                    o -> Comment.builder()
                            .post(post)
                            .content(i + " 번쨰 글 댓글 " + o)
                            .member(member)
                            .build()
            ).collect(Collectors.toList());

            commentRepository.saveAll(comments);

        });

        System.out.println("============================");
        //then
        mockMvc.perform(get("/{username}/post?page=1", member.getUsername())
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("작성글 수정")
    @Test
    void test9() throws Exception {

        //given
        Member member = memberRepository.findByUsername("username")
                .orElseThrow(MemberNotFoundException::new);

        Board board = Board.builder()
                .boardName("free")
                .build();

        boardRepository.save(board);

            Post post = Post.builder()
                    .board(board)
                    .member(member)
                    .title("제목입니다")
                    .content("내용입니다")
                    .build();

            postRepository.save(post);

            List<Comment> comments = IntStream.rangeClosed(1, 20).mapToObj(
                    o -> Comment.builder()
                            .post(post)
                            .content(o + " 번쨰 댓글")
                            .member(member)
                            .build()
            ).collect(Collectors.toList());

            commentRepository.saveAll(comments);


        PostEditRequest request = PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println("============================");

        //then
        mockMvc.perform(patch("/board/modify?id=free&no=1")
                        .with(jwt().jwt(jwt -> jwt.subject("username")))
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("수정된 제목입니다", findPost.getTitle());
        assertEquals("수정된 내용입니다", findPost.getContent());
    }
}
