package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.repository.util.BoardUtil;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.exception.custom.PostNotFoundException;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.request.post.PostEditRequest;
import com.portfolio.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Member.*;
import static com.portfolio.domain.Post.*;
import static com.portfolio.domain.util.PostEditor.*;
import static com.portfolio.request.post.PostCreateRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberUtil memberUtil;
    private final BoardUtil boardUtil;

    @Transactional
    public void write(String boardName, PostCreateRequest postCreate) {
        Board board = boardUtil.getBoard(boardName);
        Member member = memberUtil.getContextMember();
        Post post = toEntity(postCreate, member, board);
        postRepository.save(post);
    }

    public List<PostResponse> getList(String boardName, int page) {
        Board board = boardUtil.getBoard(boardName);
        return postRepository.getList(board, page, null).stream()
                .map(PostResponse::new).collect(Collectors.toList());
    }

    public PostResponse get(String boardName, Long postId) {
        Board board = boardUtil.getBoard(boardName);
        Post post = findPost(postId);
        validateBoard(post, board);
        return new PostResponse(post);
    }

    @Transactional
    public void edit(String boardName, Long postId, PostEditRequest postEdit) {
        Board board = boardUtil.getBoard(boardName);
        Post post = getValidatedPost(postId);
        validateBoard(post, board);
        editPost(postEdit, post);
    }

    @Transactional
    public void delete(String boardName, Long postId) {
        Board board = boardUtil.getBoard(boardName);
        Post post = getValidatedPost(postId);
        validateBoard(post, board);
        postRepository.delete(post);
    }

    public List<PostResponse> getListWithUsername(String username, int page) {
        Member member = memberUtil.getMember(username);
        return postRepository.getList(null, page, member).stream().map(PostResponse::new)
                .collect(Collectors.toList());
    }

    private Post getValidatedPost(Long postId) {
        Post post = findPost(postId);
        Member member = memberUtil.getContextMember();
        validatePost(post, member);
        return post;
    }


    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
    }
}
