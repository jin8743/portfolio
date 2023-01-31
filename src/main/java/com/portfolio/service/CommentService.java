package com.portfolio.service;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.exception.custom.MemberNotFoundException;
import com.portfolio.exception.custom.PostNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.MemberRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.comment.CommentCreateRequest;
import com.portfolio.request.comment.CommentEditRequest;
import com.portfolio.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Member.*;
import static com.portfolio.domain.util.CommentEditor.*;
import static com.portfolio.request.comment.CommentCreateRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public void write(Long postId, CommentCreateRequest commentCreate, String username) {
        Comment comment = createComment(postId, commentCreate, username);
        commentRepository.save(comment);
    }

    public CommentResponse get(Long commentId) {
        return new CommentResponse(findComment(commentId));
    }

    @Transactional
    public void edit(Long commentId, CommentEditRequest commentEdit, String username) {
        Comment comment = getValidatedComment(commentId, username);
        editComment(commentEdit, comment);
    }

    @Transactional
    public void delete(Long commentId, String username) {
        Comment comment = getValidatedComment(commentId, username);
        commentRepository.delete(comment);
    }

    public List<CommentResponse> getMyList(int page, String username) {
        Member member = findMember(username);
       return commentRepository.getList(page, member).stream().map(CommentResponse::new)
               .collect(Collectors.toList());
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
