package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.BoardFactory;
import com.portfolio.controller.factory.MemberFactory;
import com.portfolio.controller.factory.PostFactory;
import com.portfolio.domain.*;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.comment.CommentCreateRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.portfolio.domain.Comment.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
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
    private MockMvc mockMvc;

    @Autowired
    private MemberFactory memberFactory;
    @Autowired
    private PostFactory postFactory;
    @Autowired
    private BoardFactory boardFactory;


    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    /** 댓글 작성 */
    @DisplayName("댓글 작성 정상 요청")
    @Test
    void test1() throws Exception {


        //when
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);


        //then
        mockMvc.perform(post("/comments/{postId}", post.getId())
                        .with(user("username"))
                        .with(csrf ())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, commentRepository.count());

        Comment comment = commentRepository.findAll().get(0);
        assertEquals("댓글입니다", comment.getContent());
    }

    @DisplayName("댓글 작성시 DB에 저장된다")
    @Test
    void test121() throws Exception {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .member(memberRepository.findByUsername("username").get())
                .board(boardRepository.findById(1L).get())
                .build();

        postRepository.save(post);

        //when
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);


        //then
        mockMvc.perform(post("/comments/{postId}", post.getId())
                        .with(user("username"))
                        .with(csrf ())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, commentRepository.count());

        Comment comment = commentRepository.findAll().get(0);
        assertEquals("댓글입니다", comment.getContent());
    }

    @DisplayName("댓글 작성시 내용은 필수다")
    @Test
    void test2() throws Exception {

        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .member(memberRepository.findByUsername("username").get())
                .board(boardRepository.findById(1L).get())
                .build();

        postRepository.save(post);

        //when
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content(null)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //then
        mockMvc.perform(post("/comments/{postId}", post.getId())
                        .with(user("username"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요"))
                .andDo(print());

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("존재하지 않는 글에 댓글을 작성할수 없다")
    @Test
    void test3() throws Exception {

        //given
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //then
        mockMvc.perform(post("/comments/{postId}", 1L)
                        .with(user("username"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("대댓글 작성시 DB에 저장된다")
    @Test
    void test4() throws Exception {

        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .member(memberRepository.findByUsername("username").get())
                .board(boardRepository.findById(1L).get())
                .build();

        postRepository.save(post);

        Comment comment = builder()
                .content("부모 댓글입니다")
                .member(memberRepository.findByUsername("username").get())
                .post(post)
                .build();
        commentRepository.save(comment);

        //when
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("자식 댓글 입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println("=================================================");
        mockMvc.perform(post("/comments/child/{commentId}", comment.getId())
                        .with(user("username"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(2, commentRepository.count());
        Comment parentComment = commentRepository.findCommentWithChildCommentsById(comment.getId());
        assertEquals(1L, parentComment.getChilds().size());
    }

    @DisplayName("부모댓글이 존재하지 않을경우 대댓글을 작성할수 없다")
    @Test
    void test5() throws Exception {

        //when
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("자식 댓글 입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println("=================================================");
        mockMvc.perform(post("/comments/child/{commentId}", 1L)
                        .with(user("username"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("댓글이 존재하지 않거나 삭제되었습니다"))
                .andDo(print());

        assertEquals(0, commentRepository.count());
    }

    @DisplayName("글 단건 조회시 해당 글의 댓글과 대댓글도 같이 조회된다")
    @Test
    void test6() throws Exception {

        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .member(memberRepository.findByUsername("username").get())
                .board(boardRepository.findById(1L).get())
                .build();

        postRepository.save(post);

        IntStream.rangeClosed(1, 5).forEach(i -> {

            Member member = Member.builder()
                    .username("member " + i)
                    .password("password1234")
                    .build();
            memberRepository.save(member);

            Comment comment = builder()
                    .post(post)
                    .member(member)
                    .content(i + " 번쨰 부모 댓글입니다")
                    .build();
            commentRepository.save(comment);


            List<Comment> comments = IntStream.rangeClosed(1, i).mapToObj(
                    o -> createChildComment(member, comment,i + " 번쨰 댓글의 " + o + " 번째 대댓글" )
            ).collect(Collectors.toList());

            commentRepository.saveAll(comments);
        });

        System.out.println("=========================================");
        //then
        mockMvc.perform(get("/board/view?id=free&no={postId}", post.getId())
                        .with(user("username"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(20, commentRepository.count());
    }

    @DisplayName("특정 member가 작성한 댓글, 대댓글 페이징 조회 ")
    @Test
    void test7() throws Exception {

        //given
        Board board = Board.builder()
                .boardName("자유 게시판")
                .build();
        boardRepository.save(board);

        Post post1 = Post.builder()
                .title("첫번쨰 글의 제목입니다")
                .content("첫번쨰 글의 내용입니다")
                .member(memberRepository.findByUsername("username").get())
                .board(boardRepository.findById(1L).get())
                .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
                .title("두번쨰 글의 제목입니다")
                .content("두번쨰 글의 내용입니다")
                .member(memberRepository.findByUsername("username").get())
                .board(boardRepository.findById(2L).get())
                .build();
        postRepository.save(post2);

        Member member = memberRepository.findByUsername("username").get();

        IntStream.rangeClosed(1, 5).forEach(i -> {

            Comment comment = builder()
                    .post(post1)
                    .member(member)
                    .content(i + " 번쨰 부모 댓글입니다")
                    .build();
            commentRepository.save(comment);


            List<Comment> comments = IntStream.rangeClosed(1, i).mapToObj(
                    o -> createChildComment(member, comment, i + " 번쨰 댓글의 " + o + " 번째 대댓글")
            ).collect(Collectors.toList());
            commentRepository.saveAll(comments);
        });

        IntStream.rangeClosed(1, 5).forEach(i -> {

            Comment comment = builder()
                    .post(post2)
                    .member(member)
                    .content(i + " 번쨰 부모 댓글입니다")
                    .build();
            commentRepository.save(comment);


            List<Comment> comments = IntStream.rangeClosed(1, i).mapToObj(
                    o -> createChildComment(member, comment, i + " 번쨰 댓글의 " + o + " 번째 대댓글")
            ).collect(Collectors.toList());

            commentRepository.saveAll(comments);
        });
        System.out.println("=========================================");
        //then
       mockMvc.perform(get("/{username}/comment", member.getUsername())
                       .with(user("username"))
                       .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("존재하지 않는 member의 작성 댓글 조회시 오류가 발생한다")
    @Test
    void test8() throws Exception {

        mockMvc.perform(get("/{username}/comment", "unknownMember")
                        .with(user("username"))
                        .with(csrf())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."))
                .andDo(print());
    }
}
