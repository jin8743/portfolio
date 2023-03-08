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
import com.portfolio.request.comment.DeleteComment;
import com.portfolio.request.comment.EditComment;
import com.portfolio.request.member.SignUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static com.portfolio.domain.Comment.*;
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
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1L, commentRepository.count());
        Comment findComment = commentRepository.findCommentWithMemberAndPostById(commentRepository.findAll().get(0).getId());
        assertEquals("댓글입니다", findComment.getContent());
        assertEquals("제목", findComment.getPost().getTitle());
        assertEquals("내용", findComment.getPost().getContent());
        assertEquals("user2", findComment.getMember().getUsername());
    }

    @DisplayName("글 작성자가 댓글 작성을 허용하지 않을경우 댓글 작성할수 없다")
    @Test
    void test2() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("로그인을 하지 않은상태로 댓글을 작성할수 없다")
    @Test
    void test3() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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
        Board board = boardFactory.createBoard("free");
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

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("댓글 작성시 옳바른 형식으로 입력해야한다")
    @Test
    void test5() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(0, commentRepository.count());
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

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("댓글 작성시 글 번호는 필수다")
    @Test
    void test7() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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
        assertEquals(0, commentRepository.count());
    }


    @DisplayName("댓글 작성시 글 번호는 필수다 2")
    @Test
    void test8() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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
        assertEquals(0, commentRepository.count());
    }

    @DisplayName("댓글 작성시 내용은 필수다")
    @Test
    void test9() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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
        assertEquals(0, commentRepository.count());
    }


    @DisplayName("댓글 작성시 내용은 필수다 2")
    @Test
    void test10() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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
        assertEquals(0, commentRepository.count());
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
        assertEquals(0, commentRepository.count());
    }

    @DisplayName("삭제된 글에 댓글을 작성할수 없다")
    @Test
    void test112() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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
        assertEquals(0, commentRepository.count());
    }

    @DisplayName("글이 삭제된 경우 해당글에 달렸던 모든 댓글이 삭제된다")
    @Test
    void test245() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(0, commentRepository.count());
    }




    /**
     * 대댓글 작성
     */

    @DisplayName("대댓글 작성 정상 요청")
    @Test
    void test12() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("userN");
        Post post = postFactory.createPost(member, board, true);
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
                .andExpect(status().isOk())
                .andDo(print());

        Comment parentComment = commentRepository.findCommentWithChildCommentsById(comment.getId());
        Comment childComment = parentComment.getChilds().get(0);
        assertEquals("자식 댓글입니다", childComment.getContent());
        assertEquals(1L, parentComment.getChilds().size());
        assertEquals(2, commentRepository.count());
    }

    @DisplayName("로그인을 하지 않은 상태로 대댓글을 작성할수 없다")
    @Test
    void test13() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1, commentRepository.count());
    }

    @DisplayName("존재하지 않는 댓글에 대댓글을 작성할수 없다")
    @Test
    void test14() throws Exception {
        //given
        memberFactory.createMember("userH");
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

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("삭제된 글에 대댓글을 작성할수 없다")
    @Test
    void test1122() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("youngjin");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글입니다");

        //when
        memberFactory.createMember("jin");
        String json = objectMapper.writeValueAsString(CreateChildComment.builder()
                .parentCommentId(parentComment.getId())
                .content("댓글입니다")
                .build());
        postRepository.delete(post);

        //then
        mockMvc.perform(post("/comments/child")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("jin"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
        assertEquals(0, commentRepository.count());
    }

    @DisplayName("글이 삭제된 경우 해당글에 달렸던 모든 댓글과 대댓글이 삭제된다")
    @Test
    void test21245() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("userDD");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Comment parentComment = commentFactory.createParentComment(post, member, "댓글 " + i);
            Member newMember = memberFactory.createMember("newMemberA " + i);
            commentFactory.createChildComment(parentComment, newMember, "대댓글입니다");
        });

        //then
        mockMvc.perform(delete("/posts?id=" + post.getId())
                        .with(user("userDD"))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertEquals(0, commentRepository.count());
    }


    @DisplayName("대댓글 작성시 댓글번호는 필수다")
    @Test
    void test15() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1, commentRepository.count());
    }

    @DisplayName("대댓글 작성시 댓글번호는 필수다 2")
    @Test
    void test16() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1, commentRepository.count());
    }

    @DisplayName("대댓글 작성시 내용은 필수다")
    @Test
    void test17() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1, commentRepository.count());
    }

    @DisplayName("대댓글 작성시 내용은 필수다 2")
    @Test
    void test18() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1, commentRepository.count());
    }


    /**
     * 대댓글은 없고 댓글만 있는글  단건 조회
     * (글에 좋아요가 없는 경우)
     * 예외 상황에 대한 TestCase 는 PostControllerTest 에 있음
     * 여기에서는 정상 요청에 대한 결과만 조회
     */

    @DisplayName("댓글이 있는 글 단건 조회")
    @Test
    void test19() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("userTB");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member newMember = memberFactory.createMember("postmember" + i);
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글");
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
        });

        //then
        mockMvc.perform(get("/posts?id=" + post.getId())
                        .with(user("userTB")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.length()").value(10))
                .andExpect(jsonPath("$.comments.[0].content").value("1 번쨰 댓글"))
                .andExpect(jsonPath("$.myPost").value(true))
                .andExpect(jsonPath("$.comments.[0].username").value("postmember1"))
                .andDo(print());
        assertEquals(10, commentRepository.count());
    }

    @DisplayName("댓글이 있는 글 단건 조회 (내가 쓴 댓글인 경우)")
    @Test
    void test20() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("userTJ");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member newMember = memberFactory.createMember("postMember" + i);
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글");
            commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글 2");
        });

        //then
        mockMvc.perform(get("/posts?id=" + post.getId())
                        .with(user("postMember1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.[0].myComment").value(true))
                .andExpect(jsonPath("$.comments.[1].myComment").value(true))
                .andExpect(jsonPath("$.comments.[2].myComment").value(false))
                .andDo(print());
    }


    /**
     * 댓글과 대댓글 모두 있는글 단건 조회
     * (글에 좋아요가 없는 경우)
     * 예외 상황에 대한 TestCase 는 PostControllerTest 에 있음
     * 여기에서는 정상 요청에 대한 결과만 조회
     */

    @DisplayName("댓글과 대댓글 모두 있는글 단건 조회")
    @Test
    void test21() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("userX");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member newMember = memberFactory.createMember("AmberN" + i);
            Comment parentComment = commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글");

            IntStream.rangeClosed(1, 5).forEach(e -> {
                Member commentMember = memberFactory.createMember("CommentMemberA " + i + " " + e);
                commentFactory.createChildComment(parentComment, commentMember, i + " 번쨰 댓글의 " + e + " 번쨰 대댓글");
            });
        });

        //then
        mockMvc.perform(get("/posts?id=" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.length()").value(10))
                .andExpect(jsonPath("$.comments.[0].content").value("1 번쨰 댓글"))
                .andExpect(jsonPath("$.comments.[0].username").value("AmberN1"))
                .andExpect(jsonPath("$.comments.[0].childComments.[0].content")
                        .value("1 번쨰 댓글의 1 번쨰 대댓글"))
                .andDo(print());
        assertEquals(60, commentRepository.count());
    }


    @DisplayName("댓글과 대댓글 모두 있는글 단건 조회(내가 쓴 댓글이 있는경우)")
    @Test
    void test22() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("userZ");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member newMember = memberFactory.createMember("Amber" + i);
            Comment parentComment = commentFactory.createParentComment(post, newMember, i + " 번쨰 댓글");

            IntStream.rangeClosed(1, 9).forEach(e -> {
                Member commentMember = memberFactory.createMember("CommentMember " + i + " " + e);
                commentFactory.createChildComment(parentComment, commentMember, i + " 번쨰 댓글의 " + e + " 번쨰 대댓글");
                commentFactory.createChildComment(parentComment, newMember, "내가 쓴 댓글에 대댓글 작성함");
            });
        });

        //then
        mockMvc.perform(get("/posts?id=" + post.getId())
                        .with(user("Amber1")))
                .andExpect(status().isOk())
                .andDo(print());
    }


    /**
     * 특정 회원이 작성한 댓글, 대댓글들 페이징 조회
     */

    @DisplayName("특정 회원이 댓글만 작성한 경우 작성 댓글 조회")
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
        mockMvc.perform(get("/member/{username}/comments?page=" + 1, member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andExpect(jsonPath("$.[0].isChildComment").value(false))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + 2, member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + 1 + "&qwer=!@#", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 회원이 대댓글만 작성한 경우 작성 댓글 조회")
    @Test
    void test24() throws Exception {
        //given
        Member member = memberFactory.createMember("userABE");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abc " + i);
            Member postMember = memberFactory.createMember("ABC " + i);
            Post post = postFactory.createPost(postMember, board, true);
            Comment parentComment = commentFactory.createParentComment(post, postMember, "내용");
            IntStream.rangeClosed(1, 40).forEach(e -> {
                commentFactory.createChildComment(parentComment, member,
                        "내가 작성한 대댓글 " + i + " " + e);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/comments?page=" + 1, member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 대댓글 10 40"))
                .andExpect(jsonPath("$.[0].isChildComment").value(true))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + 2, member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + 1 + "&qwer=!@#", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 회원이 댓글과 대댓글 모두 작성했을 경우")
    @Test
    void test25() throws Exception {
        //given
        Member member = memberFactory.createMember("userABCD");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abc " + i);
            Member postMember = memberFactory.createMember("QWER " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 10).forEach(e -> {
                Comment parentComment = commentFactory.createParentComment(post, postMember, "제목입니다 " + e);
                commentFactory.createParentComment(post, member,
                        "내가 작성한 글 " + i + " " + e);
                commentFactory.createChildComment(parentComment, member,
                        "내가 작성한 대댓글 " + i + " " + e);
                IntStream.rangeClosed(1, 10).forEach(a -> {
                    commentFactory.createChildComment(parentComment, member,
                            "내가 작성한 대댓글 2번쨰 " + i + " " + e + " " + a);
                });
            });

        });

        //then
        mockMvc.perform(get("/member/{username}/comments?page=" + 1, member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + 2, member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + 1 + "&qwer=!@#", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @DisplayName("특정 회원이 작성한 댓글 조회시 페이지 정보가 없거나 잘못된 경우 1페이지를 보여준다")
    @Test
    void test26() throws Exception {
        //given
        Member member = memberFactory.createMember("findMember");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abc " + i);
            Member postMember = memberFactory.createMember("QWERW " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 20).forEach(e -> {
                commentFactory.createParentComment(post, member,
                        "내가 작성한 댓글 " + i + " " + e);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/comments", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + "qwer!@#", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?ABCD=" + "123Q" + "&qwer=!@#", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + -12, member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/comments?page=" + "100000000000000000000000000000000000000000000000000000000000000",
                        member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andExpect(jsonPath("$.[0].content").value("내가 작성한 댓글 10 20"))
                .andDo(print());
    }

    @DisplayName("내가 작성한 댓글인 경우 확인이 가능하다")
    @Test
    void test27() throws Exception {
        //given
        Member member = memberFactory.createMember("newUser1");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("abc " + i);
            Member postMember = memberFactory.createMember("QWEB " + i);
            Post post = postFactory.createPost(postMember, board, true);
            IntStream.rangeClosed(1, 20).forEach(e -> {
                commentFactory.createParentComment(post, member,
                        "내가 작성한 댓글 " + i + " " + e);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/comments", member.getUsername())
                        .with(user("newUser1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].isMyComment").value(true))
                .andExpect(jsonPath("$.[1].isMyComment").value(true))
                .andExpect(jsonPath("$.[2].isMyComment").value(true))
                .andExpect(jsonPath("$.[3].isMyComment").value(true))
                .andExpect(jsonPath("$.[4].isMyComment").value(true))
                .andExpect(jsonPath("$.[5].isMyComment").value(true))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 회원이 작성한 댓글을 페이징 조회할수 없다")
    @Test
    void test28() throws Exception {
        mockMvc.perform(get("/member/{username}/comments", "unknownMember"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."))
                .andDo(print());
    }

    @DisplayName("탈퇴한 회원이 작성한 댓글을 페이징 조회할수 없다")
    @Test
    void test29() throws Exception {
        //given
        Member member = memberFactory.createMember("userABCDEF");
        Board board = boardFactory.createBoard("abc");
        Post post = postFactory.createPost(member, board, true);
        IntStream.rangeClosed(1, 10).forEach(i -> {
            commentFactory.createParentComment(post, member, "내용");
        });
        memberRepository.delete(member);

        mockMvc.perform(get("/member/{username}/comments", member.getUsername()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."))
                .andDo(print());
    }

    /** 특정 게시판에 작성된 글 페이징 조회
     *  (댓글이 있는 경우)
     *  예외 상황에 대한 테스트 케이스는 PostControllerTest 에 있음
     *  여기에서는 정상 요청에 대한 결과만 조회
     */
    @DisplayName("특정 게시판에 작성된 글 페이징 조회 (댓글만 있고 대댓글은 없는 경우)")
    @Test
    void test12127() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member1 = memberFactory.createMember("memberW " +  + i);
            Member member2 = memberFactory.createMember("memberK " +  + i);
            Post post = postFactory.createPost(member1, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                commentFactory.createParentComment(post, member1, i + " 번쨰 글의 댓글" + e);
                commentFactory.createParentComment(post, member2, i + " 번쨰 글의 댓글" + e);
            });
        });

        //then
        mockMvc.perform(get("/posts/view?board=free&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("memberW 30"))
                .andDo(print());
    }

    @DisplayName("특정 게시판에 작성된 글 페이징 조회 (댓글과 대댓글 모두 있는 경우)")
    @Test
    void test1212127() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member1 = memberFactory.createMember("userH " +  + i);
            Member member2 = memberFactory.createMember("userJ " +  + i);
            Post post = postFactory.createPost(member1, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                Comment parentComment1 = commentFactory.createParentComment(post, member1, i + " 번쨰 글의 댓글" + e);
                Comment parentComment2 = commentFactory.createParentComment(post, member2, i + " 번쨰 글의 댓글" + e);
                commentFactory.createChildComment(parentComment1, member1, "대댓글");
                commentFactory.createChildComment(parentComment2, member2, "대댓글");
            });
        });

        //then
        mockMvc.perform(get("/posts/view?board=free&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("userH 30"))
                .andDo(print());
        assertEquals(600, commentRepository.count());
    }



    /** 특정 회원이 작성한 글 페이징 조회
     * (댓글이 있는 경우)
     *  예외 상황에 대한 테스트 케이스는 PostControllerTest 에 있음
     *  여기에서는 정상 요청에 대한 결과만 조회
     */
    @DisplayName("특정 회원이 작성한 글 여러개 조회 (댓글만 있고 대댓글은 없는 경우)")
    @Test
    void test2126() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("account123");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Post post = postFactory.createPost(member, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                Member userA = memberFactory.createMember("accountV " + i + " " + e);
                commentFactory.createParentComment(post, member, "댓글");
                commentFactory.createParentComment(post, userA, "댓글");
                commentFactory.createParentComment(post, userA, "댓글2");
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/posts?page=1", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }


    @DisplayName("특정 회원이 작성한 글 여러개 조회 (댓글과 대댓글은 모두 있는 경우)")
    @Test
    void test21226() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("account123");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Post post = postFactory.createPost(member, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                Member userA = memberFactory.createMember("accountV " + i + " " + e);
                Comment parentComment = commentFactory.createParentComment(post, member, "댓글");
                Comment parentComment2 = commentFactory.createParentComment(post, userA, "댓글");
                commentFactory.createChildComment(parentComment, userA, "대댓글");
                commentFactory.createChildComment(parentComment2, member, "댓글2");
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/posts?page=1", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }



    /**
     *  댓글 수정
     */
    @DisplayName("댓글 수정")
    @Test
    void test30() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        assertEquals(1, commentRepository.count());
        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정 후 댓글", editComment.getContent());
        assertEquals("editMember", comment.getMember().getUsername());
        assertEquals(post, comment.getPost());
    }

    @DisplayName("대댓글 수정")
    @Test
    void test31() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberA");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMember2");
        Comment beforeEdit = commentFactory.createChildComment(parentComment, editMember, "수정 전 대댓글");

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

        assertEquals(2, commentRepository.count());
        Comment afterEdit = commentRepository.findCommentWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 후 대댓글", afterEdit.getContent());
        assertEquals("editMember2", afterEdit.getMember().getUsername());
    }

    @DisplayName("댓글 수정 요청 RequestBody 에 빈값이 들어갈수 없다")
    @Test
    void test32() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }


    @DisplayName("정해진 형식으로 댓글 수정 요청을 해야한다")
    @Test
    void test33() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 수정할 댓글 번호는 필수다")
    @Test
    void test34() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 수정할 댓글 번호는 필수다 2")
    @Test
    void test35() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 수정할 댓글 번호는 필수다")
    @Test
    void test36() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberQW");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMember7");
        Comment beforeEdit = commentFactory.createChildComment(parentComment, editMember, "수정 전 대댓글");

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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 수정할 댓글 번호는 필수다 2")
    @Test
    void test37() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberQEW");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMember8");
        Comment beforeEdit = commentFactory.createChildComment(parentComment, editMember, "수정 전 대댓글");

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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 내용은 필수다")
    @Test
    void test38() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("댓글 수정 요청시 내용은 필수다")
    @Test
    void test39() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 내용은 필수다")
    @Test
    void test40() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberQER");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMemberTF");
        Comment beforeEdit = commentFactory.createChildComment(parentComment, editMember, "수정 전 대댓글");

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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("대댓글 수정 요청시 내용은 필수다")
    @Test
    void test41() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberSW");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("editMemberXZ");
        Comment beforeEdit = commentFactory.createChildComment(parentComment, editMember, "수정 전 대댓글");

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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(beforeEdit.getId());
        assertEquals("수정 전 대댓글", editComment.getContent());
    }

    @DisplayName("내가 작성하지 않은 댓글을 수정할수 없다")
    @Test
    void test42() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }

    @DisplayName("내가 작성하지 않은 대댓글을 수정할수 없다")
    @Test
    void test43() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberQSD");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member editMember = memberFactory.createMember("usernameQA");
        Comment beforeEdit = commentFactory.createChildComment(parentComment, editMember, "수정 전 대댓글");

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

        Comment afterEdit = commentRepository.findCommentWithMemberAndPostById(beforeEdit.getId());
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

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("로그인 하지 않은 상태로 댓글을 수정할수 없다")
    @Test
    void test45() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
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

        Comment editComment = commentRepository.findCommentWithMemberAndPostById(comment.getId());
        assertEquals("수정전 댓글", editComment.getContent());
    }


    /** 댓글 삭제 요청 */
    @DisplayName("댓글 삭제")
    @Test
    void test46() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("deleteMemberA");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("deleteMemberA"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("대댓글 삭제")
    @Test
    void test47() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("newMemberABCD");
        Post post = postFactory.createPost(member, board, true);
        Comment parentComment = commentFactory.createParentComment(post, member, "댓글");

        Member newMember = memberFactory.createMember("deleteMember");
        Comment childComment = commentFactory.createChildComment(parentComment, newMember, "삭제할 대댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + childComment.getId())
                        .with(user("deleteMember"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1, commentRepository.count());
    }


    @DisplayName("댓글 삭제 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식)")
    @Test
    void test48() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("editMemberCV");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

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

        assertEquals(1, commentRepository.count());
    }

    @DisplayName("존재하지 않는 댓글을 삭제할수 없다")
    @Test
    void test49() throws Exception {
        mockMvc.perform(delete("/comments?id=" + 12)
                        .with(user("editMemberCV"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("로그인 하지 않은 상태로 댓글을 삭제할수 없다")
    @Test
    void test50() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("editMemberV");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        assertEquals(1, commentRepository.count());
    }

    @DisplayName("내가 작성하지 않은 댓글을 삭제할수 없다")
    @Test
    void test51() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("editMemberL");
        Post post = postFactory.createPost(member, board, true);
        Comment comment = commentFactory.createParentComment(post, member, "삭제할 댓글");

        //then
        mockMvc.perform(delete("/comments?id=" + comment.getId())
                        .with(user("unknown"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 권한이 없습니다"))
                .andDo(print());

        assertEquals(1, commentRepository.count());
    }
}
