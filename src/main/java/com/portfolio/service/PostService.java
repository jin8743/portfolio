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
import com.portfolio.request.post.BoardSearchRequest;
import com.portfolio.request.post.PostSearchRequest;
import com.portfolio.response.post.BoardPostResponse;
import com.portfolio.response.post.MemberPostResponse;
import com.portfolio.response.post.SinglePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Board.validateBoard;
import static com.portfolio.domain.Member.*;
import static com.portfolio.domain.util.PostEditor.*;
import static com.portfolio.request.post.PostCreateRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberUtil memberUtil;
    private final BoardUtil boardUtil;


    //단건 작성
    @Transactional
    public void write(String boardName, PostCreateRequest postCreate) {
        Board board = boardUtil.getBoard(boardName);
        Member member = memberUtil.getContextMember();
        Post post = toEntity(postCreate, member, board);
        postRepository.save(post);
    }

    //    public List<SinglePostResponse> postList(PostListSearchRequest postListSearch) {
//        return postRepository.getList().stream()
//                .map(SinglePostResponse::new).collect(Collectors.toList());
//    }
//
    public List<MemberPostResponse> memberPostList(String username, Integer page) {
        Member member = memberUtil.getMember(username);
        return postRepository.memberList(member, page).stream()
                .map(MemberPostResponse::new).collect(Collectors.toList());
    }

    //단건 조회
    public SinglePostResponse get(PostSearchRequest singlePost) {
        Post post = postRepository.findWithId(singlePost.getNo());
        validateBoard(post.getBoard(), singlePost.getId());
        return new SinglePostResponse(post);
    }

    //특정 게시판 글 전체 조회
    public List<BoardPostResponse> boardPostList(BoardSearchRequest boardSearchRequest) {
       return postRepository.boardList(boardSearchRequest).stream()
                .map(BoardPostResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public void edit(PostSearchRequest postSearch, PostEditRequest postEdit) {
        Board board = boardUtil.getBoard(postSearch.getId());
        Post post = getValidatedPost(postSearch.getNo());
        validateBoard(post.getBoard(), board.getBoardName());
        editPost(postEdit, post);
    }

    @Transactional
    public void delete(PostSearchRequest postSearch) {
        Board board = boardUtil.getBoard(postSearch.getId());
        Post post = getValidatedPost(postSearch.getNo());
        validateBoard(post.getBoard(), board.getBoardName());
        postRepository.delete(post);
    }

//    public List<SinglePostResponse> getListWithUsername(String username, int page) {
//        Member member = memberUtil.getMember(username);
//        return postRepository.getList(null, page, member).stream().map(SinglePostResponse::new)
//                .collect(Collectors.toList());
//    }

//    public List<SinglePostResponse> list(PostSearchRequest postSearchRequest) {
//        return postRepository.posts(postSearchRequest.getNo()).stream()
//                .map(SinglePostResponse::new).collect(Collectors.toList());
//    }

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
