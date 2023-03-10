package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.*;
import com.portfolio.domain.Board;
import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.domain.Post;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.member.SignUp;
import com.portfolio.request.post.CreatePost;
import com.portfolio.request.post.EditPost;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberFactory memberFactory;

    @Autowired
    private BoardFactory boardFactory;

    @Autowired
    private PostFactory postFactory;

    @Autowired
    private LikeFactory likeFactory;

    @Autowired
    private CommentFactory commentFactory;

    @BeforeEach
    void clear() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    /**
     * 글 단건 작성
     */
    @DisplayName("글 작성 (댓글 작성 허용)")
    @Test
    void test1() throws Exception {
        //given
        boardFactory.createBoard("A");
        memberFactory.createMember("user1234");

        //when
        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("A")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
                        .with(user("user1234")))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
        assertTrue(post.getCommentsAllowed());
    }

    @DisplayName("글 작성 (댓글 작성 허용X)")
    @Test
    void test2() throws Exception {
        //given
        boardFactory.createBoard("B");
        memberFactory.createMember("user");

        //when
        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("B")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
                        .with(user("user")))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다", post.getTitle());
        assertEquals("내용입니다", post.getContent());
        assertFalse(post.getCommentsAllowed());
    }


    @DisplayName("로그인을 하지 않은 상태로 글을 작성할수 없다")
    @Test
    void test3() throws Exception {
        //given
        boardFactory.createBoard("C");
        memberFactory.createMember("user1234q");

        //when
        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());

        assertEquals(0, postRepository.count());
    }

    @DisplayName("글 작성시 필수 항목들을 입력해야한다")
    @Test
    void test4() throws Exception {
        //given
        memberFactory.createMember("userH");

        //then
        mockMvc.perform(post("/posts")
                        .with(csrf())
                        .with(user("userH")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 작성 요청시 옳바른 형식으로 입력해야한다")
    @Test
    void test5() throws Exception {
        //given
        memberFactory.createMember("user4");

        String json = objectMapper.writeValueAsString(
                SignUp.builder().username("username")
                        .password("1234")
                        .passwordConfirm("1")
                        .email(null).build());

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user4"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 작성시 댓글 허용여부에 대한 값이 없을경우 허용함으로 간주한다")
    @Test
    void test6() throws Exception {
        //given
        boardFactory.createBoard("D");
        memberFactory.createMember("userN");

        //when
        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("D")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(null)
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
                        .with(user("userN")))
                .andExpect(status().isOk())
                .andDo(print());

        Post post = postRepository.findAll().get(0);
        assertTrue(post.getCommentsAllowed());
    }

    @DisplayName("글 작성시 댓글 허용여부에 대한 값이 없을경우 허용함으로 간주한다 2")
    @Test
    void test7() throws Exception {
        //given
        boardFactory.createBoard("E");
        memberFactory.createMember("userL");

        //when
        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("E")
                .title("제목입니다")
                .content("내용입니다")
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
                        .with(user("userL")))
                .andExpect(status().isOk())
                .andDo(print());

        Post post = postRepository.findAll().get(0);
        assertTrue(post.getCommentsAllowed());
    }

    @DisplayName("글 작성시 게시판 이름 설정은 필수다")
    @Test
    void test8() throws Exception {
        //given
        boardFactory.createBoard("F");
        memberFactory.createMember("username2");

        CreatePost createPost = CreatePost.builder()
                .boardName(null)
                .title("제목입니다")
                .content("내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(createPost);

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("username2"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.boardName").value("글을 작성할 게시판을 선택하세요"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 작성시 게시판 이름 설정은 필수다 2")
    @Test
    void test9() throws Exception {
        //given
        boardFactory.createBoard("G");
        memberFactory.createMember("user2");

        CreatePost createPost = CreatePost.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(createPost);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("user2"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.boardName").value("글을 작성할 게시판을 선택하세요"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("존재하지 않는 게시판에 글을 작성할수 없다")
    @Test
    void test290() throws Exception {
        //given
        memberFactory.createMember("userZ");

        //when
        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
                        .with(user("userZ")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());
        assertEquals(0, postRepository.count());
    }

    @DisplayName("글 작성시 제목은 필수다")
    @Test
    void test10() throws Exception {
        //given
        boardFactory.createBoard("H");
        memberFactory.createMember("memberQ");

        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("H")
                .title(null)
                .content("내용입니다")
                .build());

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("memberQ"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력하세요"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 작성시 제목은 필수다 2")
    @Test
    void test11() throws Exception {
        //given
        boardFactory.createBoard("I");
        memberFactory.createMember("memberA");

        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("I")
                .content("내용입니다")
                .build());

        //then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("memberA"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력하세요"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 작성시 내용은 필수다")
    @Test
    void test12() throws Exception {
        //given
        boardFactory.createBoard("J");
        memberFactory.createMember("memberB");

        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("J")
                .title("제목입니다")
                .content(null)
                .build());

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("memberB"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력하세요"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 작성시 내용은 필수다")
    @Test
    void test13() throws Exception {
        //given
        boardFactory.createBoard("K");
        memberFactory.createMember("memberC");

        String json = objectMapper.writeValueAsString(CreatePost.builder()
                .boardName("K")
                .title("제목입니다")
                .build());

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("memberC"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력하세요"))
                .andDo(print());

        assertEquals(0L, postRepository.count());
    }


    /**
     * 글 단건 조회
     */
    @DisplayName("글 단건 조회")
    @Test
    void test14() throws Exception {
        //given
        Board board = boardFactory.createBoard("L");
        Member member = memberFactory.createMember("userG");
        Post post = postFactory.createPost(member, board, false);

        //then
        mockMvc.perform(
                        get("/posts?id=" + post.getId()))
                .andExpect(jsonPath("$.boardName").value("L"))
                .andExpect(jsonPath("$.nickname").value("자유게시판 L"))
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.writer").value("userG"))
                .andExpect(jsonPath("$.myPost").value(false))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(
                        get("/posts?id=" + post.getId() + "&ABC=!@#$?&가나다=2"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 단건 조회 (내가 쓴 글인경우)")
    @Test
    void test124() throws Exception {
        //given
        Board board = boardFactory.createBoard("M");
        Member member = memberFactory.createMember("user5");
        Post post = postFactory.createPost(member, board, false);

        //then
        mockMvc.perform(
                        get("/posts?id=" + post.getId())
                                .with(user("user5")))
                .andExpect(jsonPath("$.myPost").value(true))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 단건 조회 (내가 쓴 글 아닌 경우)")
    @Test
    void test1224() throws Exception {
        //given
        Board board = boardFactory.createBoard("N");
        Member member = memberFactory.createMember("userW");
        Post post = postFactory.createPost(member, board, false);

        //then
        mockMvc.perform(
                        get("/posts?id=" + post.getId())
                                .with(user("userqwer")))
                .andExpect(jsonPath("$.myPost").value(false))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 단건 조회 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식이나 값)")
    @Test
    void test15() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/posts?!@=#$&ab  @"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/posts?id=ABC!@#$"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("존재하지 않는글을 조회할수 없다")
    @Test
    void test16() throws Exception {
        mockMvc.perform(get("/posts?id=" + 123))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }


    /**
     * 특정 게시판에 작성된 글 페이징 조회
     * (글에 댓글과 좋아요가 없는 경우)
     */
    @DisplayName("특정 게시판에 작성된 글 목록 조회")
    @Test
    void test17() throws Exception {
        //given
        Board board = boardFactory.createBoard("O");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member " + i);
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=O&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member 30"))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=O&page=1&list_num=10"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=O&page=2&list_num=10&a@#!@%@a=w  sw가나다"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=O&page=10&list_num=10&a@#!@%@a=w  sw가나다"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 게시판에 작성된 글 목록 조회 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식)")
    @Test
    void test18() throws Exception {
        mockMvc.perform(get("/posts/board/view"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?1@ #ㄱㄴㄷ ^(ABC"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());
    }

    @DisplayName("특정 게시판의 글 목록 조회시 페이지 정보가 없거나 잘못된 경우 첫 페이지를 가져온다")
    @Test
    void test19() throws Exception {
        //given
        Board board = boardFactory.createBoard("P");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member1 " + i);
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=P"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=P&page=!@#$%^ ㄱ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=P&a@=!@#$%^ ㄱ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=P&page=-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=P&page=100000000000000000000000000000000000000000000000000000000000000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 글을 조회할지 설정할수 있다")
    @Test
    void test20() throws Exception {
        //given
        Board board = boardFactory.createBoard("Q");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member2 " + i);
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=Q&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=Q&size=15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(15)))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 조회에 대한 값이 없거나 형식이 잘못된 경우 20개씩 조회된다")
    @Test
    void test21() throws Exception {
        //given
        Board board = boardFactory.createBoard("R");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member3 " + i);
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=R&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=R&page=1&size=a!2@3 가나다"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=R&page=1&!@#$=@DF@ 가나다라"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 조회에 대한 값이 음수이거나 0일 경우 20개씩 조회된다")
    @Test
    void test22() throws Exception {
        //given
        Board board = boardFactory.createBoard("S");
        IntStream.rangeClosed(1, 50).forEach(i -> {
            Member member = memberFactory.createMember("member4 " + i);
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=S&page=1&list_num=-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=S&page=2&list_num=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 조회에 대한 값이 50 초과일 경우 50개씩 조회된다")
    @Test
    void test23() throws Exception {
        //given
        Board board = boardFactory.createBoard("T");
        IntStream.rangeClosed(1, 120).forEach(i -> {
            Member member = memberFactory.createMember("member5 " + i);
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=T&page=1&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(50)))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=T&page=2&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(50)))
                .andDo(print());

        mockMvc.perform(get("/posts/board/view?board=T&page=3&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 게시판의 글은 조회할수 없다")
    @Test
    void test24() throws Exception {
        mockMvc.perform(get("/posts/board/view?board={boardName}", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());
    }

    @DisplayName("작성된 글이 없는 게시판의 글 조회시 빈 ArrayList 가 반환된다")
    @Test
    void test25() throws Exception {
        //when
        boardFactory.createBoard("U");

        //then
        mockMvc.perform(get("/posts/board/view?board=U"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 게시판의 글 조회시 삭제된 글은 조회되지 않는다")
    @Test
    void test252() throws Exception {
        //when
        Board board = boardFactory.createBoard("ABCD");
        Member member = memberFactory.createMember("youngjin8743");
        IntStream.rangeClosed(1, 120).forEach(i -> {
            Post post = postFactory.createPost(member, board, true);
            postRepository.delete(post);
        });

        postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(get("/posts/board/view?board=ABCD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andDo(print());
    }


    /** 특정 게시판에 작성된 글 페이징 조회
     *  (댓글이 있는 경우)
     */

    @DisplayName("특정 게시판에 작성된 글목록 조회 (댓글만 있고 대댓글은 없는 경우)")
    @Test
    void test12127() throws Exception {
        //given
        Board board = boardFactory.createBoard("BB");
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
        mockMvc.perform(get("/posts/board/view?board=BB&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$.[0].nickname").value("자유게시판 BB"))
                .andExpect(jsonPath("$.[0].totalComments").value(10))
                .andDo(print());
    }

    @DisplayName("특정 게시판에 작성된 글목록 조회 (댓글과 대댓글 모두 있는 경우)")
    @Test
    void test1212127() throws Exception {
        //given
        Board board = boardFactory.createBoard("YUI");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member1 = memberFactory.createMember("userH " +  + i);
            Member member2 = memberFactory.createMember("userJ " +  + i);
            Post post = postFactory.createPost(member1, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                Comment parentComment1 = commentFactory.createParentComment(post, member1, i + " 번쨰 글의 댓글" + e);
                Comment parentComment2 = commentFactory.createParentComment(post, member2, i + " 번쨰 글의 댓글" + e);
                commentFactory.createChildComment(post, member1, parentComment1, "대댓글");
                commentFactory.createChildComment(post, member2, parentComment2, "대댓글");

            });
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=YUI&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$.[0].nickname").value("자유게시판 YUI"))
                .andExpect(jsonPath("$.[0].totalComments").value(20))
                .andDo(print());
    }

    @DisplayName("Soft Delete 처리된 댓글은 총 댓글수에 포함되지 않는다")
    @Test
    void test121227() throws Exception {
        //given
        Board board = boardFactory.createBoard("QWEA");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member1 = memberFactory.createMember("userQQQ " +  + i);
            Member member2 = memberFactory.createMember("userJJJ " + +i);

            Post post = postFactory.createPost(member1, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                Comment parentComment1 = commentFactory.createParentComment(post, member1, i + " 번쨰 글의 댓글" + e);
                Comment parentComment2 = commentFactory.createParentComment(post, member2, i + " 번쨰 글의 댓글" + e);
                commentFactory.createChildComment(post, member1, parentComment1, "대댓글");
                commentFactory.createChildComment(post, member2, parentComment2, "대댓글");

                commentRepository.delete(parentComment1);
            });
        });

        //then
        mockMvc.perform(get("/posts/board/view?board=QWEA&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$.[0].nickname").value("자유게시판 QWEA"))
                .andExpect(jsonPath("$.[0].totalComments").value(15))
                .andDo(print());
    }


    /**
     * 특정 회원이 작성한 글 목록 조회
     * (글에 댓글이 없는 경우)
     */
    @DisplayName("특정 회원이 작성한 글 목록 조회")
    @Test
    void test26() throws Exception {
        //given
        Board board = boardFactory.createBoard("V");
        Member member = memberFactory.createMember("account123Q");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/member/{username}/posts?page=1", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/posts?page=2", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/posts?page=3", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 회원이 작성한 글 목록 조회시 페이지 정보가 없거나 잘못된 경우 1 페이지가 조회된다")
    @Test
    void test27() throws Exception {
        //given
        Board board = boardFactory.createBoard("W");
        Member member = memberFactory.createMember("user9");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/member/{username}/posts", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/posts?page=!@#ㄱ 나ㄷ AbC", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/posts?q12w@wd%%$=!s", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/posts?page=-2", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/posts?page=1000000000000000000000000000000000000000000000000000000", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("존재하지 않는 회원의 작성글 목록을 조회할수 없다")
    @Test
    void test28() throws Exception {
        mockMvc.perform(get("/member/{username}/posts", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 회원의 작성글 목록을 조회할수 없다 2")
    @Test
    void test228() throws Exception {
        mockMvc.perform(get("/member//posts"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @DisplayName("탈퇴한 회원의 작성글 목록도 조회 가능하다")
    @Test
    void test928() throws Exception {
        Board board = boardFactory.createBoard("X");
        Member member = memberFactory.createMember("deleteMember");
        postFactory.createPost(member, board, true);
        memberRepository.delete(member);

        mockMvc.perform(get("/member/{username}/posts", "deleteMember"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("제목"))
                .andExpect(jsonPath("$.[0].totalComments").value(0))
                .andDo(print());
    }


    @DisplayName("아무 글도 작성하지 않은 회원의 작성글 목록 조회시 빈 ArrayList 가 반환된다")
    @Test
    void test29() throws Exception {
        //when
        memberFactory.createMember("youngjin");

        //then
        mockMvc.perform(get("/member/{username}/posts?page=1", "youngjin"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 회원의 작성 글 조회시 삭제된 글은 조회되지 않는다")
    @Test
    void test25232() throws Exception {
        //when
        Board board = boardFactory.createBoard("ABCDE");
        Member member = memberFactory.createMember("youngjin1234");
        IntStream.rangeClosed(1, 120).forEach(i -> {
            Post post = postFactory.createPost(member, board, true);
            postRepository.delete(post);
        });

        postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(get("/member/{username}/posts", "youngjin1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andDo(print());
    }

    /** 특정 회원이 작성한 글 페이징 조회
     * (댓글이 있는 경우)
     */
    @DisplayName("특정 회원이 작성한 글 여러개 조회 (댓글만 있고 대댓글은 없는 경우)")
    @Test
    void test2126() throws Exception {
        //given
        Board board = boardFactory.createBoard("DD");
        Member member = memberFactory.createMember("Qaccount123");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Post post = postFactory.createPost(member, board, true);
            IntStream.rangeClosed(1, 5).forEach(e -> {
                Member userA = memberFactory.createMember("accountVA " + i + " " + e);
                commentFactory.createParentComment(post, member, "댓글");
                commentFactory.createParentComment(post, userA, "댓글");
                commentFactory.createParentComment(post, userA, "댓글2");
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/posts?page=1", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$.[0].totalComments").value(15))
                .andDo(print());
    }


    @DisplayName("특정 회원이 작성한 글 여러개 조회 (댓글과 대댓글은 모두 있는 경우)")
    @Test
    void test21226() throws Exception {
        //given
        Board board = boardFactory.createBoard("EE");
        Member member = memberFactory.createMember("account123");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Post post = postFactory.createPost(member, board, true);
            IntStream.rangeClosed(1, 4).forEach(e -> {
                Member userA = memberFactory.createMember("accountV " + i + " " + e);
                Comment parentComment = commentFactory.createParentComment(post, member, "댓글");
                commentFactory.createParentComment(post, userA, "댓글");
                commentFactory.createChildComment(post, userA, parentComment, "대댓글");
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/posts?page=1", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }


    /**
     * 내가 좋아요 누른글 목록 조회
     * (댓글 없는 경우)
     */
    @DisplayName("내가 좋아요 누른글 목록 조회")
    @Test
    void test11231254() throws Exception {
        //given
        Member member = memberFactory.createMember("likeMember");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("free " + i);
            Member likeMember = memberFactory.createMember("newMemberD " + i);
            IntStream.rangeClosed(1, 10).forEach(e -> {
                Post post = postFactory.createPost(likeMember, board, true);
                likeFactory.createLike(post, member);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/likes?page=1", "likeMember")
                        .with(user("likeMember")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());
    }

    @DisplayName("내가 좋아요 누른 글 목록을 다른 이용자가 조회 할수 없다")
    @Test
    void test112231254() throws Exception {
        //given
        Member member = memberFactory.createMember("likeMemberQ");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("X " + i);
            Member likeMember = memberFactory.createMember("newMemberJ " + i);
            IntStream.rangeClosed(1, 10).forEach(e -> {
                Post post = postFactory.createPost(likeMember, board, true);
                likeFactory.createLike(post, member);
            });
        });
        //when
        memberFactory.createMember("invalidUser");

        //then
        mockMvc.perform(get("/member/{username}/likes?page=1", "likeMemberQ")
                        .with(user("invalidUser")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 권한이 없습니다"))
                .andDo(print());
    }

    @DisplayName("내가 좋아요 누른글 목록 조회시 페이지 정보가 없거나 잘못된 경우 1 페이지가 조회된다")
    @Test
    void test254() throws Exception {
        //given
        Member member = memberFactory.createMember("likeMemberB");

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Board board = boardFactory.createBoard("Y" + i);
            Member likeMember = memberFactory.createMember("likeMemberB " + i);
            IntStream.rangeClosed(1, 10).forEach(e -> {
                Post post = postFactory.createPost(likeMember, board, true);
                likeFactory.createLike(post, member);
            });
        });

        //then
        mockMvc.perform(get("/member/{username}/likes?page=", "likeMemberB")
                        .with(user("likeMemberB")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/likes", "likeMemberB")
                        .with(user("likeMemberB")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/likes?qwer=!@#abc", "likeMemberB")
                        .with(user("likeMemberB")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/likes?page=qwer", "likeMemberB")
                        .with(user("likeMemberB")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/likes?page=-12", "likeMemberB")
                        .with(user("likeMemberB")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(20))
                .andDo(print());
    }


    @DisplayName("내가 좋아요 누른 글이 없다면 빈 ArrayList 가 응답된다")
    @Test
    void test251224() throws Exception {
        //when
        memberFactory.createMember("emptyMember");

        //then
        mockMvc.perform(get("/member/{username}/likes?page=1", "emptyMember")
                        .with(user("emptyMember")))
                .andExpect(status().isOk())
                .andDo(print());
    }


    /**
     * 글 수정 요청
     */

    @DisplayName("글 수정 요청")
    @Test
    void test34() throws Exception {
        //given
        Board board = boardFactory.createBoard("Z");
        Member member = memberFactory.createMember("editmember");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editmember"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("수정된 제목입니다", findPost.getTitle());
        assertEquals("수정된 내용입니다", findPost.getContent());
        assertFalse(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청 2")
    @Test
    void test35() throws Exception {
        //given
        Board board = boardFactory.createBoard("AA");
        Member member = memberFactory.createMember("editMember");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("수정된 제목입니다", findPost.getTitle());
        assertEquals("수정된 내용입니다", findPost.getContent());
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청 3 (댓글 허용 안함에서 허용으로 변경)")
    @Test
    void test36() throws Exception {
        //given
        Board board = boardFactory.createBoard("FGH");
        Member member = memberFactory.createMember("edit1");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("edit1"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청 4 (댓글 허용에서 허용안함으로 변경)")
    @Test
    void test37() throws Exception {
        //given
        Board board = boardFactory.createBoard("CC");
        Member member = memberFactory.createMember("edit2");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("edit2"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertFalse(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청 RequestBody 에 빈값이 들어갈수 없다")
    @Test
    void test38() throws Exception {
        //given
        Board board = boardFactory.createBoard("ZXC");
        Member member = memberFactory.createMember("edit3");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .with(user("edit3"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 정해진 형식으로 전송해야한다")
    @Test
    void test39() throws Exception {
        //given
        Board board = boardFactory.createBoard("JKL");
        Member member = memberFactory.createMember("edit4");
        Post post = postFactory.createPost(member, board, true);

        /** 글 번호 값에 숫자형식이 아닌 값이 들어간 경우 */
        String json = "{\"postId\":\"abc\",\"title\":\"1\",\"content\":\"1\"," +
                "\"commentsAllowed\":false}";

        //then
        mockMvc.perform(patch("/posts")
                        .with(user("edit4"))
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 정해진 형식으로 전송해야한다 2")
    @Test
    void test40() throws Exception {
        //given
        Board board = boardFactory.createBoard("FF");
        Member member = memberFactory.createMember("editE");
        Post post = postFactory.createPost(member, board, true);

        /** Boolean 값에 알파벳이나 문자가 들어간 경우 */
        String json = "{\"title\":\"1\",\"content\":\"1\"," +
                "\"commentsAllowed\":가나다}";

        //then
        mockMvc.perform(patch("/posts")
                        .with(user("editE"))
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("서버에 전송한 정보가 형식에 맞지 않습니다"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 정해진 형식으로 전송해야한다 3")
    @Test
    void test240() throws Exception {
        //given
        Board board = boardFactory.createBoard("GG");
        Member member = memberFactory.createMember("editAB");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        /** contentType 설정 X */
        mockMvc.perform(patch("/posts")
                        .with(user("editAB"))
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message")
                        .value("지원하지 않는 미디어 유형입니다 가능한 유형: APPLICATION_JSON"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 댓글작성 허용 여부에 대한 값 변경 요청이 없을경우 기존값 유지")
    @Test
    void test41() throws Exception {
        Board board = boardFactory.createBoard("HH");
        Member member = memberFactory.createMember("new");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(null)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("new"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 댓글작성 허용 여부에 대한 값 변경 요청이 없을경우 기존값 유지 2")
    @Test
    void test42() throws Exception {
        Board board = boardFactory.createBoard("QWE");
        Member member = memberFactory.createMember("321");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("321"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertTrue(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 댓글작성 허용 여부에 대한 값 변경 요청이 없을경우 기존값 유지 3")
    @Test
    void test43() throws Exception {
        Board board = boardFactory.createBoard("II");
        Member member = memberFactory.createMember("ar");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(null)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("ar"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertFalse(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 댓글작성 허용 여부에 대한 값 변경 요청이 없을경우 기존값 유지 4")
    @Test
    void test44() throws Exception {
        Board board = boardFactory.createBoard("JJ");
        Member member = memberFactory.createMember("asd");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("asd"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertFalse(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 수정할 글 번호는 필수다")
    @Test
    void test425() throws Exception {
        //given
        Board board = boardFactory.createBoard("KK");
        Member member = memberFactory.createMember("jin123");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(null)
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("jin123"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.postId")
                        .value("수정할 글 번호가 입력되지 않았습니다"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("글 수정 요청시 수정할 글 번호는 필수다 2")
    @Test
    void test45() throws Exception {
        //given
        Board board = boardFactory.createBoard("LL");
        Member member = memberFactory.createMember("jin");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("jin"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.postId")
                        .value("수정할 글 번호가 입력되지 않았습니다"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("글 수정 요청시 제목은 필수다")
    @Test
    void test452() throws Exception {
        //given
        Board board = boardFactory.createBoard("MM");
        Member member = memberFactory.createMember("jinA");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title(null)
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("jinA"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title")
                        .value("제목을 입력해주세요"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("글 수정 요청시 제목은 필수다 2")
    @Test
    void test46() throws Exception {
        //given
        Board board = boardFactory.createBoard("NN");
        Member member = memberFactory.createMember("editMember1");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember1"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("글 수정 요청시 내용은 필수다")
    @Test
    void test47() throws Exception {
        Board board = boardFactory.createBoard("OO");
        Member member = memberFactory.createMember("editPost");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content(null)
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editPost"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("글 수정 요청시 내용은 필수다 2")
    @Test
    void test48() throws Exception {
        Board board = boardFactory.createBoard("PP");
        Member member = memberFactory.createMember("editMember2");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("editMember2"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("내가 작성하지 않은글을 수정할수 없다")
    @Test
    void test49() throws Exception {
        //given
        Board board = boardFactory.createBoard("QQ");
        Member member = memberFactory.createMember("usernameN");
        Post post = postFactory.createPost(member, board, true);
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("1234"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message")
                        .value("해당 권한이 없습니다"))
                .andDo(print());
    }

    @DisplayName("로그인하지 않은 상태로 글을 수정할수 없다")
    @Test
    void test50() throws Exception {
        //given
        Board board = boardFactory.createBoard("RR");
        Member member = memberFactory.createMember("editMember7");
        Post post = postFactory.createPost(member, board, true);
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @DisplayName("존재하지 않는 글을 수정할수 없다")
    @Test
    void test51() throws Exception {
        //when
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(123L)
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("qwer"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("삭제된 글을 수정할수 없다")
    @Test
    void test5240() throws Exception {
        //given
        Board board = boardFactory.createBoard("MJ");
        Member member = memberFactory.createMember("deleteMemberH");
        Post post = postFactory.createPost(member, board, true);

        //when
        postRepository.delete(post);
        String json = objectMapper.writeValueAsString(EditPost.builder()
                .postId(post.getId())
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("deleteMemberH"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }


    /**
     * 글 삭제 요청
     */

    @DisplayName("글 삭제")
    @Test
    void test52() throws Exception {
        //given
        Board board = boardFactory.createBoard("SS");
        Member member = memberFactory.createMember("deleteMember1234");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/posts?id={postId}", post.getId())
                        .with(user("deleteMember1234"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(0, postRepository.count());
    }

    @DisplayName("로그인하지 않은 상태로 글을 삭제할수 없다")
    @Test
    void test53() throws Exception {
        //given
        Board board = boardFactory.createBoard("TT");
        Member member = memberFactory.createMember("deleteMemberA");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/posts?id={postId}", post.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        assertEquals(1, postRepository.count());
    }

    @DisplayName("글 삭제 잘못된 요청 (필수 파라미터 값 누락 또는 잘못된 형식)")
    @Test
    void test54() throws Exception {
        //given
        Board board = boardFactory.createBoard("UU");
        Member member = memberFactory.createMember("deleteMemberD");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/posts?id=ABC!ㄱㄴㄷ")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/posts?id=")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/posts")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/posts?abc=12q&ㄱㄴㄷ=!2@2#3$5")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        assertEquals(1, postRepository.count());
    }

    @DisplayName("존재하지 않는 글을 삭제할수 없다")
    @Test
    void test55() throws Exception {
        mockMvc.perform(delete("/posts?id={postId}", 23L)
                        .with(user("member"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("내가 작성하지 않은 글을 삭제할수 없다")
    @Test
    void test56() throws Exception {
        //given
        Board board = boardFactory.createBoard("ZZ");
        Member member = memberFactory.createMember("jin1234");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/posts?id={postId}", post.getId())
                        .with(user("qwerty"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message")
                        .value("해당 권한이 없습니다"))
                .andDo(print());

        assertEquals(1L, postRepository.count());
    }

    @DisplayName("이미 삭제된 글을 삭제할수 없다")
    @Test
    void test57() throws Exception {
        //given
        Board board = boardFactory.createBoard("ERTD");
        Member member = memberFactory.createMember("jin1234AAA");
        Post post = postFactory.createPost(member, board, true);
        postRepository.delete(post);

        //then
        mockMvc.perform(delete("/posts?id={postId}", post.getId())
                        .with(user("jin1234AAA"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

}
