//package com.portfolio.service;
//
//import com.portfolio.domain.Board;
//import com.portfolio.domain.Member;
//import com.portfolio.domain.MemberRole;
//import com.portfolio.domain.Post;
//import com.portfolio.repository.member.MemberRepository;
//import com.portfolio.repository.board.BoardRepository;
//import com.portfolio.repository.comment.CommentRepository;
//import com.portfolio.repository.post.PostRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class CommentServiceTest {
//
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private BoardRepository boardRepository;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private CommentService commentService;
//
//
//    @Test
//    @DisplayName("1")
//    void test1() {
//        boardRepository.save(Board.builder().boardName("자유 게시판").build());
//        memberRepository.save(Member.builder().username("username")
//                .password("password")
//                .isEnabled(true)
//                .role(MemberRole.ROLE_MEMBER)
//                .build()
//        );
//        postRepository.save(Post.builder().title("제목입니다").content("내용입니다")
//                .member(memberRepository.findByUsername("username").get())
//                .board(boardRepository.findByBoardName("자유 게시판").get())
//                .build());
//
//        commentService.
//    }
//}
