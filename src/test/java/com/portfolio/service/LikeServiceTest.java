package com.portfolio.service;

import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LikeServiceTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MemberUtil memberUtil;

    @BeforeEach
    void clear() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        boardRepository.deleteAll();

        Member member = Member.builder()
                .username("username")
                .password("password1234")
                .build();
        memberRepository.save(member);

//        CustomUser userDetails = new CustomUser(member);

//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken(userDetails, null));
    }

    @Test
    void test1() {
        Member member = memberUtil.getContextMember();
        System.out.println(member.getUsername());
    }

}
