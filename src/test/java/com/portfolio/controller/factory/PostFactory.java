package com.portfolio.controller.factory;

import com.portfolio.domain.Board;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostFactory {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Member member, Board board, Boolean commentsAllowed) {
        Post post = Post.builder().title("제목")
                .content("내용")
                .member(member)
                .board(board)
                .commentsAllowed(commentsAllowed)
                .build();
        postRepository.save(post);
        return post;
    }
}
