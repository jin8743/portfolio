package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.BoardFactory;
import com.portfolio.controller.factory.CommentFactory;
import com.portfolio.controller.factory.MemberFactory;
import com.portfolio.controller.factory.PostFactory;
import com.portfolio.domain.*;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.comment.CreateChildComment;
import com.portfolio.request.comment.CreateComment;
import com.portfolio.request.comment.EditComment;
import com.portfolio.request.member.SignUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

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
    private MemberFactory memberFactory;

    @Autowired
    private PostFactory postFactory;

    @Autowired
    private BoardFactory boardFactory;

    @Autowired
    private CommentFactory commentFactory;

    @BeforeEach
    void clear() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
        boardRepository.deleteAll();
    }


    /**
     * 댓글 작성
     */

    @DisplayName("댓글 작성 정상 요청")
    @Test
    void test1() throws Exception {
        //given
        Board board = boardFactory.createBoard("A");
        Member member = memberFactory.createMember("userBB");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("user2");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .content("댓글입니다")
                .postId(post.getId())
                .build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user2"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(1L, commentRepository.countActiveComments());
        Comment comment = commentRepository.findAllActiveCommentWithPostAndMember().get(0);
        assertEquals("댓글입니다", comment.getContent());
        assertEquals("제목", comment.getPost().getTitle());
        assertEquals("내용", comment.getPost().getContent());
        assertEquals("user2", comment.getMember().getUsername());
    }

    @DisplayName("글 작성자가 댓글 작성을 허용하지 않을경우 댓글 작성할수 없다")
    @Test
    void test2() throws Exception {
        //given
        Board board = boardFactory.createBoard("B");
        Member member = memberFactory.createMember("user3");
        Post post = postFactory.createPost(member, board, false);

        //when
        memberFactory.createMember("user4");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .postId(post.getId())
                .content("댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user4"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("글 작성자가 댓글을 허용하지 않습니다."));

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("로그인을 하지 않은상태로 댓글을 작성할수 없다")
    @Test
    void test3() throws Exception {
        //given
        Board board = boardFactory.createBoard("C");
        Member member = memberFactory.createMember("user1");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userA");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .content("댓글입니다")
                .postId(post.getId())
                .build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("댓글 작성시 필수 항목들을 입력해야한다")
    @Test
    void test4() throws Exception {
        //given
        Board board = boardFactory.createBoard("D");
        Member member = memberFactory.createMember("user5");
        postFactory.createPost(member, board, true);

        //when
        Member commentMember = memberFactory.createMember("userHT");

        //then
        mockMvc.perform(post("/comments")
                        .with(user("userHT"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"));

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("댓글 작성시 옳바른 형식으로 입력해야한다")
    @Test
    void test5() throws Exception {
        //given
        Board board = boardFactory.createBoard("E");
        Member member = memberFactory.createMember("user6");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userHR");
        String json = objectMapper.writeValueAsString(
                SignUp.builder().username("username")
                        .password("1234")
                        .passwordConfirm("1")
                        .email(null).build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user7"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("댓글 작성시 옳바른 형식으로 입력해야한다 2")
    @Test
    void test6() throws Exception {
        //given
        /** 글 번호가 숫자 형식이 아닌경우  */
        memberFactory.createMember("user7");
        String json = "{\"postId\":\"abc\",\"content\":\"댓글입니다\"}";

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user7"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("댓글 작성시 글 번호는 필수다")
    @Test
    void test7() throws Exception {
        //given
        Board board = boardFactory.createBoard("F");
        Member member = memberFactory.createMember("userGG");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userZXC");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .postId(null)
                .content("댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userZXC"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.postId").value("글 번호가 입력되지 않았습니다"))
                .andDo(print());
        assertEquals(0, commentRepository.countActiveComments());
    }


    @DisplayName("댓글 작성시 글 번호는 필수다 2")
    @Test
    void test8() throws Exception {
        //given
        Board board = boardFactory.createBoard("G");
        Member member = memberFactory.createMember("userWW");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userB");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .content("댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userB"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.postId").value("글 번호가 입력되지 않았습니다"))
                .andDo(print());
        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("댓글 작성시 내용은 필수다")
    @Test
    void test9() throws Exception {
        //given
        Board board = boardFactory.createBoard("H");
        Member member = memberFactory.createMember("userGf");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userAWD");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .postId(post.getId())
                .content(null).build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userAWD"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());
        assertEquals(0, commentRepository.countActiveComments());
    }


    @DisplayName("댓글 작성시 내용은 필수다 2")
    @Test
    void test10() throws Exception {
        //given
        Board board = boardFactory.createBoard("I");
        Member member = memberFactory.createMember("userWF");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userASDF");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .postId(post.getId())
                .build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userASDF"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());
        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("존재하지 않는 글에 댓글을 작성할수 없다")
    @Test
    void test11() throws Exception {
        //when
        memberFactory.createMember("userC");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .postId(123L)
                .content("댓글입니다").build());

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userC"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("삭제된 글에 댓글을 작성할수 없다")
    @Test
    void test112() throws Exception {
        //given
        Board board = boardFactory.createBoard("J");
        Member member = memberFactory.createMember("userABD");
        Post post = postFactory.createPost(member, board, true);

        //when
        memberFactory.createMember("userAWEK");
        String json = objectMapper.writeValueAsString(CreateComment.builder()
                .postId(post.getId())
                .content("댓글입니다")
                .build());
        postRepository.delete(post);

        //then
        mockMvc.perform(post("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userAWEK"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("글이 삭제되더라도 글에 달렸던 댓글은 삭제되지 않는다")
    @Test
    void test245() throws Exception {
        //given
        Board board = boardFactory.createBoard("K");
        Member member = memberFactory.createMember("userCC");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            commentFactory.createParentComment(post, member, "댓글 " + i);
        });

        //then
        mockMvc.perform(delete("/posts?id=" + post.getId())
                        .with(user("userCC"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(20, commentRepository.countActiveComments());
    }



    /**
     * 대댓글 작성
     */

    @DisplayName("대댓글 작성 정상 요청")
    @Test
    void test12() throws Exception {
        //given
        Board board = boardFactory.createBoard("L");
        Member member = memberFactory.createMember("userN");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("userHGN");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(comment.getId())
                .content("자식 댓글입니다").build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userHGN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Comment parentComment = commentRepository.findWithChildCommentsById(comment.getId());
        Comment childComment = parentComment.getChilds().get(0);
        assertEquals("자식 댓글입니다", childComment.getContent());
        assertEquals(1L, parentComment.getChilds().size());
        assertEquals(2, commentRepository.countActiveComments());
    }

    @DisplayName("댓글작성을 허용하지 않는 글에 대댓글을 작성할수 없다")
    @Test
    void test132() throws Exception {
        //given
        Board board = boardFactory.createBoard("name");
        Member member = memberFactory.createMember("commentMember");
        Post post = postFactory.createPost(member, board, false);
        Comment comment = commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("userHG");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(comment.getId())
                .content("자식 댓글입니다").build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userHG"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("글 작성자가 댓글을 허용하지 않습니다."))
                .andDo(print());
    }

    @DisplayName("로그인을 하지 않은 상태로 대댓글을 작성할수 없다")
    @Test
    void test13() throws Exception {
        //given
        Board board = boardFactory.createBoard("M");
        Member member = memberFactory.createMember("user8");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("user9");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(comment.getId())
                .content("자식 댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("존재하지 않는 댓글에 대댓글을 작성할수 없다")
    @Test
    void test14() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(123L)
                .content("자식 댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .with(user("userH"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("삭제된 글에 대댓글을 작성할수 없다")
    @Test
    void test1122() throws Exception {
        //given
        Board board = boardFactory.createBoard("N");
        Member member = memberFactory.createMember("youngjin");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글입니다");

        //when
        memberFactory.createMember("jin");
        postRepository.delete(post);
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(parentComment.getId())
                .content("댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("jin"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("글이 삭제되더라도 글에 달렸던 댓글과 대댓글은 삭제되지 않는다")
    @Test
    void test21245() throws Exception {
        //given
        Board board = boardFactory.createBoard("O");
        Member member = memberFactory.createMember("userDD");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Comment parentComment = commentFactory.createParentComment(post, member, "댓글 " + i);
            Member newMember = memberFactory.createMember("newMemberA " + i);
            commentFactory.createChildComment(post, newMember, parentComment, "대댓글입니다");
        });

        //then
        mockMvc.perform(delete("/posts?id=" + post.getId())
                        .with(user("userDD"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(40, commentRepository.countActiveComments());
    }


    @DisplayName("대댓글 작성시 댓글번호는 필수다")
    @Test
    void test15() throws Exception {
        //given
        Board board = boardFactory.createBoard("P");
        Member member = memberFactory.createMember("userJ");
        Post post = postFactory.createPost(member, board, true);
        commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("userQWE");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(null)
                .content("자식 댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userQWE"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.parentCommentId").value("대댓글을 작성할 댓글번호가 입력되지 않았습니다"))
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("대댓글 작성시 댓글번호는 필수다 2")
    @Test
    void test16() throws Exception {
        //given
        Board board = boardFactory.createBoard("Q");
        Member member = memberFactory.createMember("userAS");
        Post post = postFactory.createPost(member, board, true);
        commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("user15");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .content("자식 댓글입니다")
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user15"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.parentCommentId").value("대댓글을 작성할 댓글번호가 입력되지 않았습니다"))
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("대댓글 작성시 내용은 필수다")
    @Test
    void test17() throws Exception {
        //given
        Board board = boardFactory.createBoard("R");
        Member member = memberFactory.createMember("userY");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("userQW");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(comment.getId())
                .content(null)
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userQW"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("대댓글 작성시 내용은 필수다 2")
    @Test
    void test18() throws Exception {
        //given
        Board board = boardFactory.createBoard("S");
        Member member = memberFactory.createMember("userT");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "부모댓글");

        //when
        memberFactory.createMember("user14");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(comment.getId())
                .build());

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("userT"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }


    /**
     * 특정 글에 달린 댓글 목록 조회
     * (댓글만 있고 대댓글은 없는 경우)
     */
    @DisplayName("특정 글에 달린 댓글들 페이징 조회")
    @Test
    void test19() throws Exception {
        //given
        Board board = boardFactory.createBoard("T");
        Member member = memberFactory.createMember("userTB");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member newMember = memberFactory.createMember("QQQ" + i);
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 1");
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
        });

        //then
        mockMvc.perform(get("/comments?id={postId}&page={page}", post.getId(), 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("1 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("QQQ1"))
                .andDo(print());

        mockMvc.perform(get("/comments?id={postId}&page={page}", post.getId(), 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("11 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("QQQ11"))
                .andDo(print());
    }

    @DisplayName("특정 글에 달린 댓글들 목록 조회시 내가 작성한 댓글인지 여부 확인 가능하다")
    @Test
    void test1123419() throws Exception {
        //given
        Board board = boardFactory.createBoard("U");
        Member member = memberFactory.createMember("abcde");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member newMember = memberFactory.createMember("memberA " + i);
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 1");
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
        });

        //then
        mockMvc.perform(get("/comments?id={postId}&page={page}", post.getId(), 1)
                        .with(user("memberA 1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].isMyComment").value(true))
                .andExpect(jsonPath("$.[1].isMyComment").value(true))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 글의 댓글들을 조회할수 없다")
    @Test
    void test20() throws Exception {
        mockMvc.perform(get("/comments?id={postId}&page=1", 123))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("특정 글에 달린 댓글들 조회시 페이지 정보가 없거나 잘못된 경우 1 페이지를 조회한다")
    @Test
    void test1912() throws Exception {
        //given
        Board board = boardFactory.createBoard("V");
        Member member = memberFactory.createMember("userABC");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member newMember = memberFactory.createMember("postmemberW" + i);
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 1");
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
        });

        //then
        mockMvc.perform(get("/comments?id={postId}&page=", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("1 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("postmemberW1"))
                .andDo(print());

        mockMvc.perform(get("/comments?id={postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("1 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("postmemberW1"))
                .andDo(print());

        mockMvc.perform(get("/comments?id={postId}&page=", post.getId(), "qwer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("1 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("postmemberW1"))
                .andDo(print());

        mockMvc.perform(get("/comments?id={postId}&zxcv={page}", post.getId(), "qwer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("1 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("postmemberW1"))
                .andDo(print());

        mockMvc.perform(get("/comments?id={postId}&page={page}", post.getId(), -12))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("1 번쨰 댓글 1"))
                .andExpect(jsonPath("$.[0].username").value("postmemberW1"))
                .andDo(print());
    }

    @DisplayName("특정 글에 달린 댓글들 조회시 글 번호는 필수다")
    @Test
    void test1129() throws Exception {
        //given
        Board board = boardFactory.createBoard("W");
        Member member = memberFactory.createMember("userQB");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member newMember = memberFactory.createMember("postQ" + i);
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 1");
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
        });

        //then
        mockMvc.perform(get("/comments?id="))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/comments"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    /**
     * 특정 글에 달린 댓글들 목록 조회
     * (댓글과 대댓글 모두 있는 경우)
     */
    @DisplayName("특정 글에 달린 댓글들 목록 조회")
    @Test
    void test11239() throws Exception {
        //given
        Board board = boardFactory.createBoard("X");
        Member member = memberFactory.createMember("stock");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member newMember = memberFactory.createMember("ZZZ" + i);
            Comment parentComment = commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 1");
            Comment parentComment1 = commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
            commentFactory.createChildComment(post, member, parentComment, i + " 번쨰 대댓글입니다 ");
            commentFactory.createChildComment(post, newMember, parentComment, "대댓글 입니다");
            commentFactory.createChildComment(post, member, parentComment1, i + " 번쨰 대댓글입니다 ");
            commentFactory.createChildComment(post, newMember, parentComment1, "대댓글 입니다");
        });


        //then
        mockMvc.perform(get("/comments?id={postId}&page={page}", post.getId(), 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());
    }

    @DisplayName("댓글이 삭제되더라도 해당 댓글에 달린 대댓글은 조회 가능하다")
    @Test
    void test11231239() throws Exception {

        //given
        Board board = boardFactory.createBoard("Y");
        Member member = memberFactory.createMember("userQWER");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member newMember = memberFactory.createMember("postmemberQ" + i);
            Comment parentComment = commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 1");
            commentFactory.createChildComment(post, member, parentComment, i + " 번쨰 대댓글입니다 ");
            Comment childComment = commentFactory.createChildComment(post, newMember, parentComment, "대댓글 입니다");

            commentRepository.delete(parentComment);
            commentRepository.delete(childComment);
        });


        //then
        mockMvc.perform(get("/comments?id={postId}&page={page}", post.getId(), 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());
    }



    /**
     * 내가 작성한 댓글, 대댓글들 페이징 조회
     * 타인이 조회 시도시 인가 예외 발생
     */

    @DisplayName("내가 댓글만 작성한 경우 작성 댓글 목록  조회")
    @Test
    void test23() throws Exception {
        //given
        Member member = memberFactory.createMember("userABCDE");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abc " + i);
            Member postMember = memberFactory.createMember("QWERA " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 20).forEach(e -> {
                commentFactory.createParentComment(post, member,
                        "내가 작성한 댓글 " + i + " " + e);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/comments?page=" + 1, member.getUsername())
                        .with(user("userABCDE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andExpect(jsonPath("$.[0].postTitle").value("제목"))
                .andExpect(jsonPath("$.[0].postEnabled").value(true))
                .andExpect(jsonPath("$.[0].isChildComment").value(false))
                .andDo(print());;
    }

    @DisplayName("내가 대댓글만 작성한 경우 작성 대댓글 목록 조회")
    @Test
    void test24() throws Exception {
        //given
        Member member = memberFactory.createMember("userABE");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abcf " + i);
            Member postMember = memberFactory.createMember("ABC " + i);
            Post post = postFactory.createPost(postMember, board, true);
            Comment parentComment = commentFactory.createParentComment(post, postMember, "내용");
            IntStream.rangeClosed(1, 40).forEach(e -> {
                commentFactory.createChildComment(post, member, parentComment,
                        "내가 작성한 대댓글 " + i + " " + e);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/comments?page=" + 1, member.getUsername())
                        .with(user("userABE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 대댓글 10 40"))
                .andExpect(jsonPath("$.[0].postTitle").value("제목"))
                .andExpect(jsonPath("$.[0].postEnabled").value(true))
                .andExpect(jsonPath("$.[0].isChildComment").value(true))
                .andDo(print());
    }

    @DisplayName("내가 댓글과 대댓글 모두 작성했을 경우 작성 댓글, 대댓글 목록 조회")
    @Test
    void test25() throws Exception {
        //given
        Member member = memberFactory.createMember("userABCD");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abck " + i);
            Member postMember = memberFactory.createMember("QWER " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 10).forEach(e -> {
                Comment parentComment = commentFactory.createParentComment(post, postMember, "제목입니다 " + e);
                commentFactory.createParentComment(post, member,
                        "내가 작성한 글 " + i + " " + e);
                commentFactory.createChildComment(post, member, parentComment,
                        "내가 작성한 대댓글 " + i + " " + e);
                IntStream.rangeClosed(1, 10).forEach(a -> {
                    commentFactory.createChildComment(post, member, parentComment,
                            "내가 작성한 대댓글 2번쨰 " + i + " " + e + " " + a);
                });
            });

        });

        //then
        mockMvc.perform(get("/member/{username}/comments?page=" + 1, member.getUsername())
                        .with(user("userABCD")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 대댓글 2번쨰 10 10 10"))
                .andExpect(jsonPath("$.[0].postTitle").value("제목"))
                .andExpect(jsonPath("$.[0].postEnabled").value(true))
                .andDo(print());
    }


    @DisplayName("내가 작성한 댓글 목록 조회시 페이지 정보가 없거나 잘못된 경우 1페이지를 보여준다")
    @Test
    void test26() throws Exception {
        //given
        Member member = memberFactory.createMember("findMember");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abcd " + i);
            Member postMember = memberFactory.createMember("QWERW " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 20).forEach(e -> {
                commentFactory.createParentComment(post, member,
                        "내가 작성한 댓글 " + i + " " + e);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/comments", member.getUsername())
                        .with(user("findMember")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + "qwer!@#", member.getUsername())
                        .with(user("findMember")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?ABCD=" + "123Q" + "&qwer=!@#", member.getUsername())
                        .with(user("findMember")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + -12, member.getUsername())
                        .with(user("findMember")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + "100000000000000000000000000000000000000000000000000000000000000",
                        member.getUsername())
                        .with(user("findMember")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());
    }

    @DisplayName("내가 댓글을 작성한 글이 삭제되더라도 댓글은 조회 가능하다")
    @Test
    void test27() throws Exception {
        //given
        Member member = memberFactory.createMember("newUser1");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abce " + i);
            Member postMember = memberFactory.createMember("QWEB " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 20).forEach(e -> {
                commentFactory.createParentComment(post, member,
                        "내가 작성한 댓글 " + i + " " + e);
            });
            postRepository.delete(post);
        });

        //then
        mockMvc.perform(get("/member/{username}/comments", member.getUsername())
                        .with(user("newUser1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andExpect(jsonPath("$.[0].postEnabled").value(false))
                .andExpect(jsonPath("$.[0].postTitle").value("삭제된 글입니다"))
                .andExpect(jsonPath("$.[1].content").value("내가 작성한 댓글 10 19"))
                .andExpect(jsonPath("$.[1].postEnabled").value(false))
                .andExpect(jsonPath("$.[1].postTitle").value("삭제된 글입니다"))
                .andDo(print());
    }






    /**
     *  댓글 수정
     */
    @DisplayName("댓글 수정")
    @Test
    void test30() throws Exception {
        //given
        Board board = boardFactory.createBoard("FF");
        Member member = memberFactory.createMember("editMember");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정 후 댓글", editComment.getContent());
        assertEquals("editMember", comment.getMember().getUsername());
        assertEquals(post, comment.getPost());
    }

    @DisplayName("대댓글 수정")
    @Test
    void test31() throws Exception {
        //given
        Board board = boardFactory.createBoard("GG");
        Member member = memberFactory.createMember("newMemberA");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMember2");
        Comment beforeEdit = commentFactory.createChildComment(post, editMember, parentComment, "수정 전 대댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(beforeEdit.getId())
                .content("수정 후 대댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember2"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(2, commentRepository.countActiveComments());
        Comment afterEdit = commentRepository.findWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 후 대댓글", afterEdit.getContent());
        assertEquals("editMember2", afterEdit.getMember().getUsername());
    }

    @DisplayName("댓글 수정 요청 RequestBody 에 빈값이 들어갈수 없다")
    @Test
    void test32() throws Exception {
        //given
        Board board = boardFactory.createBoard("HH");
        Member member = memberFactory.createMember("editQWE");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .with(user("editQWE"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }


    @DisplayName("정해진 형식으로 댓글 수정 요청을 해야한다")
    @Test
    void test33() throws Exception {
        //given
        Board board = boardFactory.createBoard("II");
        Member member = memberFactory.createMember("editMemberQWE");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        /** 댓글 번호에 숫자형식이 아닌 값이 입력될 경우 */
        String json = "{\"commentId\":\"abc\",\"content\":\"수정 후 대댓글\"}";

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberQWE"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 수정할 댓글 번호는 필수다")
    @Test
    void test34() throws Exception {
        //given
        Board board = boardFactory.createBoard("JJ");
        Member member = memberFactory.createMember("editMember3");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(null)
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember3"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.commentId").value("수정할 댓글번호가 입력되지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 수정할 댓글 번호는 필수다 2")
    @Test
    void test35() throws Exception {
        //given
        Board board = boardFactory.createBoard("KK");
        Member member = memberFactory.createMember("editMember4");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember4"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.commentId").value("수정할 댓글번호가 입력되지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 수정할 댓글 번호는 필수다")
    @Test
    void test36() throws Exception {
        //given
        Board board = boardFactory.createBoard("LL");
        Member member = memberFactory.createMember("newMemberQW");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMember7");
        Comment beforeEdit = commentFactory.createChildComment(post, editMember, parentComment, "수정 전 대댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(null)
                .content("수정 후 대댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember7"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.commentId").value("수정할 댓글번호가 입력되지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 수정할 댓글 번호는 필수다 2")
    @Test
    void test37() throws Exception {
        //given
        Board board = boardFactory.createBoard("MM");
        Member member = memberFactory.createMember("newMemberQEW");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMember8");
        Comment beforeEdit = commentFactory.createChildComment(post, editMember, parentComment, "수정 전 대댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .content("수정 후 대댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember8"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.commentId").value("수정할 댓글번호가 입력되지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 내용은 필수다")
    @Test
    void test38() throws Exception {
        //given
        Board board = boardFactory.createBoard("NN");
        Member member = memberFactory.createMember("editMemberTR");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content(null)
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberTR"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.content").value("내용을 입력하지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 내용은 필수다")
    @Test
    void test39() throws Exception {
        //given
        Board board = boardFactory.createBoard("OO");
        Member member = memberFactory.createMember("editMemberQS");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberQS"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.content").value("내용을 입력하지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 내용은 필수다")
    @Test
    void test40() throws Exception {
        //given
        Board board = boardFactory.createBoard("PP");
        Member member = memberFactory.createMember("newMemberQER");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMemberTF");
        Comment beforeEdit = commentFactory.createChildComment(post, editMember, parentComment, "수정 전 대댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(beforeEdit.getId())
                .content(null)
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberTF"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.content").value("내용을 입력하지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 내용은 필수다")
    @Test
    void test41() throws Exception {
        //given
        Board board = boardFactory.createBoard("QQ");
        Member member = memberFactory.createMember("newMemberSW");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMemberXZ");
        Comment beforeEdit = commentFactory.createChildComment(post, editMember, parentComment, "수정 전 대댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(beforeEdit.getId())
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberXZ"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.content").value("내용을 입력하지 않았습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("내가 작성하지 않은 댓글을 수정할수 없다")
    @Test
    void test42() throws Exception {
        //given
        Board board = boardFactory.createBoard("RR");
        Member member = memberFactory.createMember("editMemberQWER");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("1234"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 권한이 없습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("내가 작성하지 않은 대댓글을 수정할수 없다")
    @Test
    void test43() throws Exception {
        //given
        Board board = boardFactory.createBoard("SS");
        Member member = memberFactory.createMember("newMemberQSD");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("usernameQA");
        Comment beforeEdit = commentFactory.createChildComment(post, editMember, parentComment, "수정 전 대댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(beforeEdit.getId())
                .content("수정 후 대댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("zxc"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 권한이 없습니다"))
                .andDo(print());

        Comment afterEdit = commentRepository.findWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", afterEdit.getContent());
    }

    @DisplayName("존재하지 않는 댓글을 수정할수 없다")
    @Test
    void test44() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(12L)
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("1234"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("로그인 하지 않은 상태로 댓글을 수정할수 없다")
    @Test
    void test45() throws Exception {
        //given
        Board board = boardFactory.createBoard("TT");
        Member member = memberFactory.createMember("editMemberBG");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("삭제된 글에 달린 댓글을 수정할수 없다")
    @Test
    void test4512() throws Exception {
        //given
        Board board = boardFactory.createBoard("AB");
        Member member = memberFactory.createMember("editMember111");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        postRepository.delete(post);
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember111"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        Comment editComment = commentRepository.findWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }


    @DisplayName("삭제된 나의 댓글을 수정할수 없다")
    @Test
    void test4512212() throws Exception {
        //given
        Board board = boardFactory.createBoard("ABQ");
        Member member = memberFactory.createMember("editMemberGGG");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");
        commentRepository.delete(comment);

        //when
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberGGG"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("삭제된 나의 대댓글을 수정할수 없다")
    @Test
    void test45122() throws Exception {
        //given
        Board board = boardFactory.createBoard("ABQD");
        Member member = memberFactory.createMember("editMemberGGGQ");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "수정전 댓글");
        Comment childComment = commentFactory.createChildComment(post, member, parentComment, "대댓글");



        //when
        commentRepository.delete(childComment);
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(childComment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMemberGGGQ"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("삭제된 타인의 댓글을 수정할수 없다")
    @Test
    void test4519912() throws Exception {
        //given
        Board board = boardFactory.createBoard("bat");
        Member member = memberFactory.createMember("ironman");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        commentRepository.delete(comment);
        memberFactory.createMember("superman");
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(comment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("superman"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("삭제된 타인의 대댓글을 수정할수 없다")
    @Test
    void test49912() throws Exception {
        //given
        Board board = boardFactory.createBoard("money");
        Member member = memberFactory.createMember("rich");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "수정전 댓글");
        Comment childComment = commentFactory.createChildComment(post, member, parentComment, "대댓글");


        //when
        commentRepository.delete(childComment);
        memberFactory.createMember("gold");
        String json = objectMapper.writeValueAsString(EditComment.builder()
                .commentId(childComment.getId())
                .content("수정 후 댓글")
                .build());

        //then
        mockMvc.perform(patch("/comments")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("gold"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }


    /** 댓글 삭제 요청 */
    @DisplayName("댓글 삭제")
    @Test
    void test46() throws Exception {
        //given
        Board board = boardFactory.createBoard("UU");
        Member member = memberFactory.createMember("deleteMemberA");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("deleteMemberA"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(0, commentRepository.countActiveComments());
    }

    @DisplayName("대댓글 삭제")
    @Test
    void test47() throws Exception {
        //given
        Board board = boardFactory.createBoard("VV");
        Member member = memberFactory.createMember("newMemberABCD");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member newMember = memberFactory.createMember("deleteMember");
        Comment childComment = commentFactory.createChildComment(post, newMember, parentComment, "삭제할 대댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + childComment.getId())
                        .with(user("deleteMember"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }


    @DisplayName("댓글 삭제 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식)")
    @Test
    void test48() throws Exception {
        //given
        Board board = boardFactory.createBoard("WW");
        Member member = memberFactory.createMember("editMemberCV");
        Post post = postFactory.createPost(member, board, true);
        commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments")
                        .with(user("editMemberCV"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/comments?id=" + "qwer!2ㄱㄴㄷ")
                        .with(user("editMemberCV"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/comments?abc=" + "qwer!2ㄱㄴㄷ")
                        .with(user("editMemberCV"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("존재하지 않는 댓글을 삭제할수 없다")
    @Test
    void test49() throws Exception {
        mockMvc.perform(delete("/comments?id=" + 1221345667)
                        .with(user("editMemberCV"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("이미 삭제된 내 댓글을 삭제시도 할수 없다")
    @Test
    void test59() throws Exception {
        //given
        Board board = boardFactory.createBoard("ABN");
        Member member = memberFactory.createMember("batman");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");
        commentRepository.delete(comment);

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("batman"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("이미 삭제된 타인의 댓글을 삭제시도 할수 없다")
    @Test
    void test5129() throws Exception {
        //given
        Board board = boardFactory.createBoard("mouth");
        Member member = memberFactory.createMember("nice");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //when
        commentRepository.delete(comment);
        memberFactory.createMember("good");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("good"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("로그인 하지 않은 상태로 댓글을 삭제할수 없다")
    @Test
    void test50() throws Exception {
        //given
        Board board = boardFactory.createBoard("XX");
        Member member = memberFactory.createMember("editMemberV");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("내가 작성하지 않은 댓글을 삭제할수 없다")
    @Test
    void test51() throws Exception {
        //given
        Board board = boardFactory.createBoard("YY");
        Member member = memberFactory.createMember("deleteMemberAS");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("unknown"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 권한이 없습니다"));

        assertEquals(1, commentRepository.countActiveComments());
    }

    @DisplayName("삭제된 글에 달린 댓글도 삭제할수 있다")
    @Test
    void test451212() throws Exception {
        //given
        Board board = boardFactory.createBoard("ABC");
        Member member = memberFactory.createMember("deleteMember1234");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "수정전 댓글");

        //when
        postRepository.delete(post);

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("deleteMember1234"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(0, commentRepository.countActiveComments());
    }
}
