package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.BoardFactory;
import com.portfolio.controller.factory.MemberFactory;
import com.portfolio.controller.factory.PostFactory;
import com.portfolio.domain.Board;
import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.domain.Post;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.member.SignUpRequest;
import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.request.post.PostEditRequest;
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
    private CommentRepository commentRepository;
    @Autowired
    private MemberFactory memberFactory;
    @Autowired
    private BoardFactory boardFactory;
    @Autowired
    private PostFactory postFactory;


    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    /** 글 단건 작성 */
    @DisplayName("글 작성 (댓글 작성 허용)")
    @Test
    void test1() throws Exception {
        //given
        boardFactory.createBoard("free");
        memberFactory.createMember("user1234");

        //when
        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("user");

        //when
        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("user1234q");

        //when
        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(post("/write")
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
        mockMvc.perform(post("/write")
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
                SignUpRequest.builder().username("username")
                        .password("1234")
                        .passwordConfirm("1")
                        .email(null).build());

        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("userN");

        //when
        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(null)
                .build());

        //then
        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("userG");

        //when
        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .content("내용입니다")
                .build());

        //then
        mockMvc.perform(post("/write")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
                        .with(user("userG")))
                .andExpect(status().isOk())
                .andDo(print());

        Post post = postRepository.findAll().get(0);
        assertTrue(post.getCommentsAllowed());
    }

    @DisplayName("글 작성시 게시판 이름 설정은 필수다")
    @Test
    void test8() throws Exception {
        //given
        boardFactory.createBoard("free");
        memberFactory.createMember("username2");

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .boardName(null)
                .title("제목입니다")
                .content("내용입니다")
                .build();
        String json = objectMapper.writeValueAsString(postCreateRequest);

        //then
        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("user2");

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        String json = objectMapper.writeValueAsString(postCreateRequest);

        mockMvc.perform(post("/write")
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

    @DisplayName("글 작성시 제목은 필수다")
    @Test
    void test10() throws Exception {
        //given
        boardFactory.createBoard("free");
        memberFactory.createMember("memberQ");

        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title(null)
                .content("내용입니다")
                .build());

        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("memberA");

        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .content("내용입니다")
                .build());

        //then
        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("memberB");

        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .content(null)
                .build());

        mockMvc.perform(post("/write")
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
        boardFactory.createBoard("free");
        memberFactory.createMember("memberC");

        String json = objectMapper.writeValueAsString(PostCreateRequest.builder()
                .boardName("free")
                .title("제목입니다")
                .build());

        mockMvc.perform(post("/write")
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



    /** 글 단건 조회 */
    @DisplayName("글 단건 조회")
    @Test
    void test14() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("user5");
        Post post = postFactory.createPost(member, board, false);

        //then
        mockMvc.perform(
                get("/board/view?id="+ post.getId()))
                .andExpect(jsonPath("$.boardName").value("free"))
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.writer").value("user5"))
                .andExpect(jsonPath("$.myPost").value(false))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(
                get("/board/view?id="+ post.getId() + "&ABC=!@#$?&가나다=2"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("글 단건 조회 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식이나 값)")
    @Test
    void test15() throws Exception {
        mockMvc.perform(get("/board/view"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/board/view?!@=#$&ab  @"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/board/view?id=ABC!@#$"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("존재하지 않는글을 조회할수 없다")
    @Test
    void test16() throws Exception {
        mockMvc.perform(get("/board/view?id=" + 123))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }





    /** 특정 게시판에 작성된 글 페이징 조회 */
    @DisplayName("특정 게시판에 작성된 글 여러개 조회")
    @Test
    void test17() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member " + i);
            Post post = postFactory.createPost(member, board, true);
            postRepository.save(post);
        });

        //then
        mockMvc.perform(get("/board/lists?board=free&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member 30"))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=1&list_num=10"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=2&list_num=10&a@#!@%@a=w  sw가나다"))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=10&list_num=10&a@#!@%@a=w  sw가나다"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 게시판에 작성된 글 여러개 조회 잘못된 요청 (필수 파라미터 누락 또는 잘못된 형식)")
    @Test
    void test18() throws Exception {
        mockMvc.perform(get("/board/lists"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());

        mockMvc.perform(get("/board/lists?1@ #ㄱㄴㄷ ^(ABC"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());
    }

    @DisplayName("특정 게시판의 글 조회시 페이지 정보가 없거나 잘못된 경우 첫 페이지를 가져온다")
    @Test
    void test19() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member1 " + i);
            Post post = postFactory.createPost(member, board, true);
            postRepository.save(post);
        });

        //then
        mockMvc.perform(get("/board/lists?board=free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=!@#$%^ ㄱ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].username").value("member1 30"))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&a@=!@#$%^ ㄱ"))
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
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member2 " + i);
            Post post = postFactory.createPost(member, board, true);
            postRepository.save(post);
        });

        //then
        mockMvc.perform(get("/board/lists?board=free&list_num=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&list_num=15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(15)))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 조회에 대한 값이 없거나 형식이 잘못된 경우 20개씩 조회된다")
    @Test
    void test21() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Member member = memberFactory.createMember("member3 " + i);
            Post post = postFactory.createPost(member, board, true);
            postRepository.save(post);
        });

        //then
        mockMvc.perform(get("/board/lists?board=free&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=1&list_num=a!2@3 가나다"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=1&!@#$=@DF@ 가나다라"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 조회에 대한 값이 음수이거나 0일 경우 20개씩 조회된다")
    @Test
    void test22() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 50).forEach(i -> {
            Member member = memberFactory.createMember("member4 " + i);
            Post post = postFactory.createPost(member, board, true);
            postRepository.save(post);
        });

        //then
        mockMvc.perform(get("/board/lists?board=free&page=1&list_num=-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=2&list_num=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }

    @DisplayName("몇개 단위로 조회에 대한 값이 50 초과일 경우 50개씩 조회된다")
    @Test
    void  test23() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        IntStream.rangeClosed(1, 120).forEach(i -> {
            Member member = memberFactory.createMember("member5 " + i);
            Post post = postFactory.createPost(member, board, true);
            postRepository.save(post);
        });

        //then
        mockMvc.perform(get("/board/lists?board=free&page=1&list_num=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(50)))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=2&list_num=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(50)))
                .andDo(print());

        mockMvc.perform(get("/board/lists?board=free&page=3&list_num=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andDo(print());
    }

    @DisplayName("존재하지 않는 게시판의 글은 조회할수 없다")
    @Test
    void test24() throws Exception {
        mockMvc.perform(get("/board/lists?board={boardName}", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("알수 없는 게시판 이름 입니다"))
                .andDo(print());
    }

    @DisplayName("작성된 글이 없는 게시판의 글 조회시 빈 ArrayList 가 반환된다")
    @Test
    void test25() throws Exception {
        //when
        boardFactory.createBoard("free");

        //then
        mockMvc.perform(get("/board/lists?board=free"))
                .andExpect(status().isOk())
                .andDo(print());
    }



    /** 특정 member 가 작성한 글 페이징 조회 */
    @DisplayName("특정 member 가 작성한 글 여러개 조회")
    @Test
    void test26() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("account123");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/member/{username}/post?page=1", member.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(20)))
                .andExpect(jsonPath("$[0].title").value("제목"))
                .andExpect(jsonPath("$[0].boardName").value("free"))
                .andDo(print());

        mockMvc.perform(get("/member/{username}/post?page=2", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/post?page=3", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("특정 member 가 작성한 글 페이징 조회시 페이지 정보가 없거나 잘못된 경우 1 페이지가 조회된다")
    @Test
    void test27() throws Exception {
        //given
        Board board = boardFactory.createBoard("free");
        Member member = memberFactory.createMember("user9");
        IntStream.rangeClosed(1, 30).forEach(i -> {
            postFactory.createPost(member, board, true);
        });

        //then
        mockMvc.perform(get("/member/{username}/post", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/post?page=!@#ㄱ 나ㄷ AbC", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/member/{username}/post?q12w@wd%%$=!s", member.getUsername()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("존재하지 않는 member 의 작성글은 조회할수 없다")
    @Test
    void test28() throws Exception {
        mockMvc.perform(get("/member/{username}/post", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."))
                .andDo(print());
    }

    @DisplayName("아무 글도 작성하지 않은 member 의 작성글 조회시 빈 ArrayList 가 반환된다")
    @Test
    void test29() throws Exception {
        //when
        memberFactory.createMember("youngjin");

        //then
        mockMvc.perform(get("/member/{username}/post?page=1", "youngjin"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    /** 수정할 글 조회 */
    @DisplayName("수정할 글 조회")
    @Test
    void test30() throws Exception {
        //given
        Member member = memberFactory.createMember("abc");
        Board board = boardFactory.createBoard("free");
        Post post = Post.builder()
                .board(board)
                .member(member)
                .title("제목입니다")
                .content("내용입니다")
                .commentsAllowed(false)
                .build();
        postRepository.save(post);

        //then
        mockMvc.perform(get("/modify?id=" + post.getId())
                        .with(user("abc")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value("제목입니다"))
                .andExpect(jsonPath("$.content").value("내용입니다"))
                .andExpect(jsonPath("$.commentsAllowed").value(false))
                .andDo(print());

        mockMvc.perform(get("/modify?id=" + post.getId() + "&!WCE가나다")
                        .with(user("abc")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value("제목입니다"))
                .andExpect(jsonPath("$.content").value("내용입니다"))
                .andExpect(jsonPath("$.commentsAllowed").value(false))
                .andDo(print());
    }

    @DisplayName("수정할 글 조회 잘못된 요청 (필수 파라미터가 누락 또는 잘못된 형식)")
    @Test
    void test31() throws Exception {
        //given
        Member member = memberFactory.createMember("account");
        Board board = boardFactory.createBoard("free");
        Post post = Post.builder()
                .board(board)
                .member(member)
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        //then
        mockMvc.perform(get("/modify")
                        .with(user("account")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/modify?id=!@#$AB 가나")
                        .with(user("account")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(get("/modify?ㄱㄴㄷ=ㅃ!Abv@#4^")
                        .with(user("account")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("존재하지 않는글을 수정하기 위해 조회할수 없다")
    @Test
    void test32() throws Exception {
        mockMvc.perform(get("/modify?id=123")
                        .with(user("abc")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }

    @DisplayName("내가 작성하지 않은 글을 수정하기 위해 조회할수 없다")
    @Test
    void test33() throws Exception {
        //given
        Member member = memberFactory.createMember("abcd");
        Board board = boardFactory.createBoard("free");
        Post post = postFactory.createPost(member, board, true);

        mockMvc.perform(get("/modify?id=" + post.getId())
                        .with(user("member123")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("해당 권한이 없습니다"))
                .andDo(print());
    }

    /** 글 수정 요청 */
    @DisplayName("글 수정 요청")
    @Test
    void test34() throws Exception {
        //given
        Member member = memberFactory.createMember("editmember");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("editMember");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("edit1");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(true)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("edit2");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("edit2"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertFalse(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 필수 항목들을 입력해야한다")
    @Test
    void test38() throws Exception {
        //given
        Member member = memberFactory.createMember("edit3");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("edit4");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        /** boolean 값에 알파벳이나 문자가 들어간 경우 */
        String json = "{\"title\":\"1\",\"content\":\"1\"," +
                "\"commentsAllowed\":가나다}";

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("editAB");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(false)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("new");
        Board board = boardFactory.createBoard("free");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(null)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("321");
        Board board = boardFactory.createBoard("free");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("ar");
        Board board = boardFactory.createBoard("free");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .commentsAllowed(null)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("asd");
        Board board = boardFactory.createBoard("free");
        Post post = postFactory.createPost(member, board, false);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("asd"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertFalse(findPost.getCommentsAllowed());
    }

    @DisplayName("글 수정 요청시 제목은 필수다")
    @Test
    void test45() throws Exception {
        //given
        Member member = memberFactory.createMember("jin");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest.builder()
                .title(null)
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("jin"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andDo(print());

        Post findPost = postRepository.findById(post.getId()).get();
        assertEquals("제목", findPost.getTitle());
        assertEquals("내용", findPost.getContent());
    }

    @DisplayName("글 수정 요청시 제목은 필수다 2")
    @Test
    void test46() throws Exception {
        //given
        Member member = memberFactory.createMember("editMember1");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest.builder()
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("editPost");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest.builder()
                .title("수정된 제목입니다")
                .content(null)
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("editMember2");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //when
        String json = objectMapper.writeValueAsString(PostEditRequest.builder()
                .title("수정된 제목입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("usernameN");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);
        String json = objectMapper.writeValueAsString(PostEditRequest.builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        Member member = memberFactory.createMember("editMember7");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);
        String json = objectMapper.writeValueAsString(PostEditRequest.builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", post.getId())
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
        String json = objectMapper.writeValueAsString(PostEditRequest
                .builder()
                .title("수정된 제목입니다")
                .content("수정된 내용입니다")
                .build());

        //then
        mockMvc.perform(patch("/modify?id={postId}", 9L)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .with(user("qwer"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());
    }


    /** 글 삭제 요청 */
    @DisplayName("글 삭제")
    @Test
    void test52() throws Exception {
        //given
        Member member = memberFactory.createMember("deleteMember");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/delete?id={postId}",post.getId())
                        .with(user("deleteMember"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
        assertEquals(0, postRepository.count());
    }

    @DisplayName("로그인하지 않은 상태로 글을 삭제할수 없다")
    @Test
    void test53() throws Exception {
        //given
        Member member = memberFactory.createMember("deleteMemberA");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/delete?id={postId}",post.getId())
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        assertEquals(1, postRepository.count());
    }

    @DisplayName("글 삭제 잘못된 요청 (필수 파라미터 값 누락 또는 잘못된 형식)")
    @Test
    void test54() throws Exception {
        //given
        Member member = memberFactory.createMember("deleteMemberD");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/delete?id=ABC!ㄱㄴㄷ")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/delete?id=")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/delete")
                        .with(user("deleteMemberD"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        mockMvc.perform(delete("/delete?abc=12q&ㄱㄴㄷ=!2@2#3$5")
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
        mockMvc.perform(delete("/delete?id={postId}", 23L)
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
        Member member = memberFactory.createMember("jin1234");
        Board board = boardFactory.createBoard("abcd");
        Post post = postFactory.createPost(member, board, true);

        //then
        mockMvc.perform(delete("/delete?id={postId}", post.getId())
                        .with(user("qwerty"))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message")
                        .value("해당 권한이 없습니다"))
                .andDo(print());

        assertEquals(1L, postRepository.count());
    }
}
