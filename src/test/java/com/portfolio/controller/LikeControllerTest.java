package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.*;
import com.portfolio.domain.*;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.like.CreateLike;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static com.portfolio.domain.MemberRole.ROLE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostFactory postFactory;

    @Autowired
    private MemberFactory memberFactory;

    @Autowired
    private LikeFactory likeFactory;

    @Autowired
    private BoardFactory boardFactory;


    @BeforeEach
    void clear() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
        memberRepository.deleteAll();
        boardRepository.deleteAll();
    }


    /**
     * ????????? ??????
     */

    @DisplayName("????????? ?????? ????????????")
    @Test
    void test1() throws Exception {
        //given
        Board board = boardFactory.createBoard("CVB");
        Member member = memberFactory.createMember("qwer");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(post("/likes?postId=" + post.getId())
                        .with(user("qwer"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1, likeRepository.count());
        Long likeId = likeRepository.findAll().get(0).getId();
        Like like = likeRepository.findWithPostAndMemberById(likeId);
        assertEquals("qwer", like.getMember().getUsername());
        assertEquals("??????", like.getPost().getContent());
    }

    @DisplayName("????????? ?????? ????????? ?????? (?????? ???????????? ?????? ?????? ????????? ??????)")
    @Test
    void test2() throws Exception {

        //given
        Board board = boardFactory.createBoard("FGH");
        Member member = memberFactory.createMember("jin");
        postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(post("/likes")
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ???????????? ????????? ?????????????????????"))
                .andDo(print());

        mockMvc.perform(post("/likes?postId=" + "zxcv")
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ???????????? ????????? ?????????????????????"))
                .andDo(print());

        mockMvc.perform(post("/likes?abcd=" + 123)
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ???????????? ????????? ?????????????????????"))
                .andDo(print());

        assertEquals(0, likeRepository.count());
    }

    @DisplayName("???????????? ?????? ?????? ????????? ???????????? ????????? ??????")
    @Test
    void test3() throws Exception {
        //given
        Board board = boardFactory.createBoard("AA");
        Member member = memberFactory.createMember("qwer1");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(post("/likes?postId=" + post.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated())
                .andDo(print());

        assertEquals(0, likeRepository.count());
    }

    @DisplayName("???????????? ?????? ?????? ???????????? ????????? ??????")
    @Test
    void test4() throws Exception {
        //when
        memberFactory.createMember("asdf");

        //then
        mockMvc.perform(post("/likes?postId=" + 123)
                        .with(user("asdf"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ???????????? ????????? ?????????????????????"))
                .andDo(print());
        assertEquals(0, likeRepository.count());
    }

    @DisplayName("?????? ???????????? ???????????? ???????????? ???????????? ????????? ??????")
    @Test
    void test5() throws Exception {
        //given
        Board board = boardFactory.createBoard("abc");
        Member member = memberFactory.createMember("qwer2");
        Post post = postFactory.createPost(member, board, true);
        likeFactory.createLike(post, member);

        //then
        mockMvc.perform(post("/likes?postId=" + post.getId())
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("?????? ???????????? ?????? ????????????."))
                .andDo(print());
        assertEquals(1, likeRepository.count());
    }

    /**
     * ????????? ??????
     */
    @DisplayName("????????? ?????? ?????? ??????")
    @Test
    void test6() throws Exception {
        //given
        Board board = boardFactory.createBoard("ASD");
        Member member = memberFactory.createMember("youngjin");
        Post post = postFactory.createPost(member, board, true);
        likeFactory.createLike(post, member);

        //when
        mockMvc.perform(delete("/likes?postId=" + post.getId())
                        .with(user("youngjin"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(0, likeRepository.count());
    }

    @DisplayName("???????????? ?????? ?????? ????????? ????????? ?????? ??????")
    @Test
    void test7() throws Exception {
        //when
        mockMvc.perform(delete("/likes?postId=" +123)
                        .with(user("abcd"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ???????????? ????????? ?????????????????????"))
                .andDo(print());

        //then
        assertEquals(0, likeRepository.count());
    }

    @DisplayName("???????????? ????????? ?????? ?????? ????????? ????????? ?????? ??????")
    @Test
    void test8() throws Exception {
        //given
        Board board = boardFactory.createBoard("abcd");
        Member member = memberFactory.createMember("QQQ");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/likes?postId=" + post.getId())
                        .with(user("QQQ"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("?????? ?????? ???????????? ????????? ???????????????"))
                .andDo(print());
    }

    @DisplayName("????????? ?????? ???????????? ?????? ???????????? ??????")
    @Test
    void test9() throws Exception {
        //given
        Board board = boardFactory.createBoard("OOO");
        Member member = memberFactory.createMember("bbb");
        Post post = postFactory.createPost(member, board, true);
        likeFactory.createLike(post, member);

        //when
        memberFactory.createMember("WWW");

        //then
        mockMvc.perform(delete("/likes?postId=" + post.getId())
                        .with(user("WWW"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("?????? ?????? ???????????? ????????? ???????????????"))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ?????? ????????? ????????? ????????? ?????? ??????")
    @Test
    void test10() throws Exception {
        //given
        Board board = boardFactory.createBoard("EEE");
        Member member = memberFactory.createMember("LLL");
        Post post = postFactory.createPost(member, board, true);
        likeFactory.createLike(post, member);

        //then
        mockMvc.perform(delete("/likes?postId=" + post.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    /**
     * ????????? ????????? ??????
     */

    @DisplayName("???????????? ?????? ??? ????????? ?????? ?????? ???????????? ???????????? ??????")
    @Test
    void test11() throws Exception {
        //given
        Board board = boardFactory.createBoard("LLL");
        Member member = memberFactory.createMember("ABC");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member factoryMember = memberFactory.createMember("likeA " + i);
            likeFactory.createLike(post, factoryMember);
        });

        //then
        mockMvc.perform(get("/likes?postId=" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLikes").value(100))
                .andExpect(jsonPath("$.likedPost").value(false))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ??? ????????? ?????? - ?????? ???????????? ?????? ?????? ")
    @Test
    void test12() throws Exception {
        //given
        Board board = boardFactory.createBoard("TTT");
        Member member = memberFactory.createMember("NNN");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member factoryMember = memberFactory.createMember("likeNNN " + i);
            likeFactory.createLike(post, factoryMember);
        });
        likeFactory.createLike(post, member);

        //then
        mockMvc.perform(get("/likes?postId=" + post.getId())
                        .with(user("NNN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLikes").value(101))
                .andExpect(jsonPath("$.likedPost").value(true))
                .andDo(print());
    }

    @DisplayName("????????? ???????????? ??? ????????? ????????? ???????????? ?????????")
    @Test
    void test13() throws Exception {
        //given
        Board board = boardFactory.createBoard("LCD");
        Member member = memberFactory.createMember("ABCQQ");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 50).forEach(i -> {
            Member factoryMember = memberFactory.createMember("likeAB1 " + i);
            Like like = likeFactory.createLike(post, factoryMember);
            likeRepository.delete(like);
        });

        //when
        IntStream.rangeClosed(1, 50).forEach(i -> {
            Member factoryMember = memberFactory.createMember("likeBB " + i);
            likeFactory.createLike(post, factoryMember);
        });

        //then
        mockMvc.perform(get("/likes?postId=" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLikes").value(50))
                .andDo(print());
    }

    @DisplayName("???????????? ?????? ?????? ??? ????????? ????????? ?????? ???????????? ???????????? ???????????? ??????")
    @Test
    void test14() throws Exception {
        mockMvc.perform(get("/likes?postId=" + 111111))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("???????????? ???????????? ????????? ?????????????????????"))
                .andDo(print());
    }
}
