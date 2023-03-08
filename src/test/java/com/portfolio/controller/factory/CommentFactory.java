package com.portfolio.controller.factory;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.comment.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentFactory {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createParentComment(Post post, Member member, String content) {
        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .build();
        commentRepository.save(comment);

        return comment;
    }

    public Comment createChildComment(Comment parentComment, Member member, String content) {
        Comment comment = Comment.builder()
                .parentComment(parentComment)
                .member(member)
                .content(content)
                .build();
        commentRepository.save(comment);

        return comment;
    }
}
