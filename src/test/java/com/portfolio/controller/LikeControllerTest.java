package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.*;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.post.PostRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.portfolio.domain.MemberRole.ROLE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LikeControllerTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    void init() {
        Board board = Board.builder()
                .boardName("free")
                .build();
        boardRepository.save(board);

        Member member = Member.builder()
                .username("username")
                .password("password1234")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .member(member)
                .board(board)
                .build();

        postRepository.save(post);
    }

    @BeforeEach
    void clear() {
        likeRepository.deleteAll();
    }

    @DisplayName("/likes 요청시 DB에 값이 저장된다")
    @Test
    void test1() throws Exception {
        //when
        mockMvc.perform(post("/likes/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        Like like = likeRepository.findById(1L).get();
        Post post = postRepository.findById(1L).get();

        assertEquals(1, likeRepository.count());
        assertEquals("username", like.getMember().getUsername());
        assertEquals("제목입니다", like.getPost().getTitle());
        assertEquals(1, post.getLikes());
    }

    @DisplayName("이미 좋아요를 누른 글에 한번더 좋아요를 누를경우 " +
            "기존 좋아요가 삭제된다")
    @Test
    void test2() throws Exception {
        //given
        likeRepository.save(Like.builder()
                .post(postRepository.findById(1L).get())
                .member(memberRepository.findByUsername("username").get())
                .build());


        //when
        mockMvc.perform(post("/likes/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        Post post = postRepository.findById(1L).get();
        assertEquals(0, likeRepository.count());
        assertEquals(0, post.getLikes());
    }


//    @Test
//    @DisplayName("동시성 문제 확인")
//    void test3() throws InterruptedException {
//
//        List<Member> members = IntStream.rangeClosed(1, 100).mapToObj(i ->
//                        Member.builder()
//                                .username("username " + i)
//                                .password("password " + i)
//                                .isEnabled(true)
//                                .role(MemberRole.ROLE_MEMBER)
//                                .build())
//                .collect(Collectors.toList());
//        memberRepository.saveAll(members);
//
//
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            executorService.submit(() -> {
//                try {
//                    mockMvc.perform(post("/likes/{postId}", 1L)
//                                    .with(jwt().jwt(jwt -> jwt.subject("username " + i)))
//                                    .contentType(APPLICATION_JSON))
//                } finally {
//                    latch.countDown();
//
//                }
//            });
//        }
//
//        latch.await();
//
//        Post post = postRepository.findById(1L).get();
//        assertEquals(100, post.getLikes());
//    }



}
