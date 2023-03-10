package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.BoardFactory;
import com.portfolio.controller.factory.CommentFactory;
import com.portfolio.controller.factory.MemberFactory;
import com.portfolio.controller.factory.PostFactory;
import com.portfolio.domain.Board;
import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.board.CreateBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardFactory boardFactory;

    @Autowired
    private MemberFactory memberFactory;

    @Autowired
    private PostFactory postFactory;

    @Autowired
    private CommentFactory commentFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clear() {
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    /** 게시판 생성 작성 */
    @DisplayName("게시판 생성")
    @Test
    void test1() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(CreateBoard.builder()
                .boardName("free")
                .nickname("자유게시판")
                .build());

        //then
        mockMvc.perform(post("/admin/board")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userBB").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());

        Board board = boardRepository.findByBoardName("free");
        assertEquals("자유게시판", board.getNickname());
    }

    @DisplayName("게시판 생성시 이름은 필수다")
    @Test
    void test2() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(CreateBoard.builder()
                .boardName(null)
                .nickname("자유게시판")
                .build());

        //then
        mockMvc.perform(post("/admin/board")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userBB").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.boardName").value("게시판 이름을 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("게시판 생성시 별칭은 필수다")
    @Test
    void test3() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(CreateBoard.builder()
                .boardName("abc")
                .nickname(null)
                .build());

        //then
        mockMvc.perform(post("/admin/board")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userCC").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.nickname").value("게시판 별칭을 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("게시판 생성은 관리자만 할수있다")
    @Test
    void test4() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(CreateBoard.builder()
                .boardName("abcd")
                .nickname("자유게시판 A")
                .build());

        //then
        mockMvc.perform(post("/admin/board")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userDD").roles("MEMBER"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    /**
     * 회원 조회
     */

    @DisplayName("회원 정보 목록 조회")
    @Test
    void test5() throws Exception {
        //when
        IntStream.rangeClosed(1, 100).forEach(i -> {
            memberFactory.createMember("member " + i);
        });

        //then
        mockMvc.perform(get("/admin/members?page=" + 1)
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(50))
                .andExpect(jsonPath("$.[0].username").value("member 1"))
                .andExpect(jsonPath("$.[0].email").value("member 1@naver.com"))
                .andDo(print());
    }

    @DisplayName("회원 정보 목록 조회시 페이지 정보가 없거나 잘못된 형식인 경우 1페이지가 조회된다")
    @Test
    void test6() throws Exception {
        //when
        IntStream.rangeClosed(1, 100).forEach(i -> {
            memberFactory.createMember("memberQ " + i);
        });

        //then
        mockMvc.perform(get("/admin/members?page=")
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(50))
                .andExpect(jsonPath("$.[0].username").value("memberQ 1"))
                .andExpect(jsonPath("$.[0].email").value("memberQ 1@naver.com"));

        mockMvc.perform(get("/admin/members")
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/members?page=" + "QWER")
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/members?abc=" + "dfg")
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/members?page=" + -5)
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 정보 목록 조회시 회원이 없는경우 빈 ArrayList 가 조회된다")
    @Test
    void test7() throws Exception {
        mockMvc.perform(get("/admin/members")
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(jsonPath("$.size()").value(0))
                .andDo(print());
    }

    @DisplayName("회원 1명에 대한 정보 조회")
    @Test
    void test8() throws Exception {
        //when
        Member member = memberFactory.createMember("ABCD");

        //then
        mockMvc.perform(get("/admin/members/{username}", member.getUsername())
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.isEnabled").value(true))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 회원의 정보를 조회할수 없다")
    @Test
    void test9() throws Exception {
        mockMvc.perform(get("/admin/members/{username}", "abd")
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."))
                .andDo(print());
    }

    @DisplayName("탈퇴한 회원의 정보도 조회할수 있다")
    @Test
    void test10() throws Exception {
        //when
        Member member = memberFactory.createMember("ZZZ");
        memberRepository.delete(member);

        //then
        mockMvc.perform(get("/admin/members/{username}", member.getUsername())
                        .with(user("userDD").roles("ADMIN")))
                .andExpect(jsonPath("$.id").value(member.getId()))
                .andExpect(jsonPath("$.username").value(member.getUsername()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andExpect(jsonPath("$.isEnabled").value(false));
    }

    /**
     * 글 삭제
     */
    @DisplayName("특정 회원이 작성한 글을 삭제할수 있다")
    @Test
    void test11() throws Exception {
        //when
        Board board = boardFactory.createBoard("qwe");
        Member member = memberFactory.createMember("deleteMemberAAA");
        Post post = postFactory.createPost(member, board, true);


        //then
        mockMvc.perform(delete("/admin/posts?id=" + post.getId())
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(0, postRepository.count());
    }

    @DisplayName("존재하지 않는글을 삭제할수 없다")
    @Test
    void test12() throws Exception {
        mockMvc.perform(delete("/admin/posts?id=" + 123)
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("이미 삭제된 글을 삭제 처리할수 없다")
    @Test
    void test13() throws Exception {
        //when
        Board board = boardFactory.createBoard("BNM");
        Member member = memberFactory.createMember("DFG");
        Post post = postFactory.createPost(member, board, true);
        postRepository.delete(post);

        //then
        mockMvc.perform(delete("/admin/posts?id=" + post.getId())
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    /**
     * 회원 강퇴
     */
    @DisplayName("회원 강퇴 처리")
    @Test
    void test14() throws Exception {
        //when
        Member member = memberFactory.createMember("QQWER");

        //then
        mockMvc.perform(delete("/admin/members/{username}", member.getUsername())
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(0, memberRepository.countActiveMember());
    }

    @DisplayName("존재하지 않는 회원을 강퇴처리 할수 없다")
    @Test
    void test15() throws Exception {
        mockMvc.perform(delete("/admin/members/{username}", "Nobody")
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."));
    }

    @DisplayName("이미 탈퇴한 회원을 강퇴 처리 할수 없다")
    @Test
    void test16() throws Exception {
        //when
        Member member = memberFactory.createMember("QQWERTYYU");
        memberRepository.delete(member);

        //then
        mockMvc.perform(delete("/admin/members/{username}", member.getUsername())
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."));

        assertEquals(0, memberRepository.countActiveMember());
    }

    /**
     * 댓글 삭제
     */
    @DisplayName("특정 회원이 작성한 댓글을 삭제할수 있다")
    @Test
    void test17() throws Exception {
        //when
        Board board = boardFactory.createBoard("qlo");
        Member member = memberFactory.createMember("deleteMemberAAA");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "댓글입니다");

        //then
        mockMvc.perform(delete("/admin/comments?id=" + comment.getId())
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("존재하지 않는 댓글을 삭제할수 없다")
    @Test
    void test18() throws Exception {
        mockMvc.perform(delete("/admin/comments?id=" + 1234)
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"));
    }

    @DisplayName("이미 삭제된 댓글을 삭제할수 없다")
    @Test
    void test19() throws Exception {
        //when
        Board board = boardFactory.createBoard("DFGHTY");
        Member member = memberFactory.createMember("deleteMemberATA");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "댓글입니다");
        commentRepository.delete(comment);

        //then
        mockMvc.perform(delete("/admin/comments?id=" + comment.getId())
                        .with(user("userDD").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"));
    }

}
