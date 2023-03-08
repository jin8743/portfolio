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

import static com.portfolio.domain.MemberRole.ROLE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private CommentFactory commentFactory;

    @Autowired
    private BoardFactory boardFactory;




    @AfterEach
    void clear() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
        memberRepository.deleteAll();
        boardRepository.deleteAll();
    }


    /**
     * 좋아요 요청
     */

    @DisplayName("좋아요 요청 정상처리")
    @Test
    void test1() throws Exception {

        //given
        Board board = boardFactory.createBoard("abc");
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
        assertEquals("내용", like.getPost().getContent());
    }

    @DisplayName("좋아요 요청 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식)")
    @Test
    void test2() throws Exception {

        //given
        Board board = boardFactory.createBoard("abc");
        Member member = memberFactory.createMember("qwer2");
        postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(post("/likes")
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(post("/likes?postId=" + "zxcv")
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(post("/likes?abcd=" + 123)
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        assertEquals(0, likeRepository.count());
    }

    @DisplayName("로그인을 하지 않은 상태로 좋아요를 누를수 없다")
    @Test
    void test4() throws Exception {
        //given
        Board board = boardFactory.createBoard("abc");
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

    @DisplayName("존재하지 않는 글에 좋아요를 누를수 없다")
    @Test
    void test5() throws Exception {
        //when
        memberFactory.createMember("asdf");

        //then
        mockMvc.perform(post("/likes?postId=" + 123)
                        .with(user("asdf"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
        assertEquals(0, likeRepository.count());
    }

    @DisplayName("이미 좋아요를 누른글에 다시한번 좋아요를 누를수 없다")
    @Test
    void test12() throws Exception {
        //given
        Board board = boardFactory.createBoard("abc");
        Member member = memberFactory.createMember("qwer2");
        Post post = postFactory.createPost(member, board, true);
        likeFactory.createLike(post, member);

        //then
        mockMvc.perform(post("/likes?postId=" + post.getId())
                        .with(user("qwer2"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
        assertEquals(1, likeRepository.count());
    }

    /**
     * 좋아요 취소
     */
    @DisplayName("좋아요 취소 정상 요청")
    @Test
    void test6() throws Exception {
        //given
        Board board = boardFactory.createBoard("abc");
        Member member = memberFactory.createMember("qwer1");
        Post post = postFactory.createPost(member, board, true);
        likeFactory.createLike(post, member);

        //when
        mockMvc.perform(delete("/likes?postId=" + post.getId())
                        .with(user("qwer1"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$."))
                .andDo(print());

        //then
        assertEquals(0, likeRepository.count());
    }
}
