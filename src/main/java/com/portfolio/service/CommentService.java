package com.portfolio.service;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.exception.custom.MemberNotFoundException;
import com.portfolio.exception.custom.PostNotFoundException;
import com.portfolio.repository.CommentRepository;
import com.portfolio.repository.MemberRepository;
import com.portfolio.repository.PostRepository;
import com.portfolio.request.CommentCreateRequest;
import com.portfolio.request.CommentEditRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.domain.Member.*;
import static com.portfolio.request.CommentCreateRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public void save(Long postId, CommentCreateRequest commentCreate, String username) {
        Comment comment = createComment(postId, commentCreate, username);
        commentRepository.save(comment);
    }

    @Transactional
    public void edit(Long commentId, CommentEditRequest commentEdit, String username) {
        Comment comment = getValidatedComment(commentId, username);

    }

    private Comment getValidatedComment(Long commentId, String username) {
        Comment comment = findComment(commentId);
        Member member = findMember(username);
        return validateComment(comment, member);
    }

    private Comment createComment(Long postId, CommentCreateRequest commentCreate, String username) {
        Post post = findPost(postId);
        Member member = findMember(username);
        return toEntity(commentCreate, post, member);
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(PostNotFoundException::new);
    }
}
