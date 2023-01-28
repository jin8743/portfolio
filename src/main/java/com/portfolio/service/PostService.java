package com.portfolio.service;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.exception.custom.MemberNotFoundException;
import com.portfolio.exception.custom.PostNotFoundException;
import com.portfolio.repository.MemberRepository;
import com.portfolio.repository.PostRepository;
import com.portfolio.request.PostCreateRequest;
import com.portfolio.request.PostEditRequest;
import com.portfolio.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Member.*;
import static com.portfolio.domain.util.PostEditor.*;
import static com.portfolio.request.PostCreateRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void write(PostCreateRequest postCreate, String username) {
        Member member = findMember(username);
        Post post = toEntity(postCreate, member);
        postRepository.save(post);
    }

    public List<PostResponse> getList(int page) {
        return postRepository.getList(page, null).stream()
                .map(PostResponse::new).collect(Collectors.toList());
    }

    public PostResponse get(Long postId) {
        return new PostResponse(findPost(postId));
    }

    @Transactional
    public void edit(Long postId, PostEditRequest postEdit, String username) {
        Post post = getValidatedPost(postId, username);
        editPost(postEdit, post);
    }

    @Transactional
    public void delete(Long postId, String username) {
        Post post = getValidatedPost(postId, username);
        postRepository.delete(post);
    }

    public List<PostResponse> getMyList(int page, String username) {
        Member member = findMember(username);
        return postRepository.getList(page, member).stream().map(PostResponse::new)
                .collect(Collectors.toList());
    }

    private Post getValidatedPost(Long postId, String username) {
        Post post = findPost(postId);
        Member member = findMember(username);
        return validatePost(post, member);
    }



    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }
}
