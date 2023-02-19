package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.util.BoardUtil;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.member.PageRequest;
import com.portfolio.request.post.*;
import com.portfolio.response.post.BoardPostResponse;
import com.portfolio.response.post.MemberPostResponse;
import com.portfolio.response.post.SinglePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Board.validateBoard;
import static com.portfolio.domain.Member.*;
import static com.portfolio.domain.Post.*;
import static com.portfolio.domain.editor.PostEditor.*;
import static com.portfolio.request.post.PostCreateRequest.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberUtil memberUtil;
    private final BoardUtil boardUtil;


    /** 조회 기능 */
    //단건 조회
    public SinglePostResponse get(PostSearchRequest singlePost) {
        Post post = postRepository.findWithId(singlePost.getNo());
        checkNull(post);
        validateBoard(post.getBoard(), singlePost.getId());
        return new SinglePostResponse(post);
    }

    //특정 게시판 글 페이징 조회
    public List<BoardPostResponse> boardPostList(BoardSearchRequest boardSearchRequest) {
        boardUtil.boardExists(boardSearchRequest.getId());
        return postRepository.boardList(boardSearchRequest).stream()
                .map(BoardPostResponse::new).collect(Collectors.toList());
    }

    //특정 member 작성글 페이징 조회
    public List<MemberPostResponse> memberPostList(String username, PageRequest request) {
        Member member = memberUtil.getMember(username);
        return postRepository.memberList(member, request.getPage()).stream()
                .map(MemberPostResponse::new).collect(Collectors.toList());
    }

    /** 작성 기능 */
    @Transactional
    public void write(String boardName, PostCreateRequest postCreate) {
        Board board = boardUtil.getBoard(boardName);
        Member member = memberUtil.getContextMember();
        Post post = toPost(postCreate, member, board);
        postRepository.save(post);
    }

    /** 수정 기능 */
    @Transactional
    public void edit(PostSearchRequest postSearch, PostEditRequest postEdit) {
        Post post = getValidatedPost(postSearch);
        editPost(postEdit, post);
    }


    /** 삭제 기능 */
    @Transactional
    public void delete(PostSearchRequest postSearch) {
        Post post = getValidatedPost(postSearch);
        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    private Post getValidatedPost(PostSearchRequest request) {
        Post post = postRepository.findValidationPost(request.getNo());
        Member member = memberUtil.getContextMember();
        validatePost(post, member);
        validateBoard(post.getBoard(), request.getId());
        return post;
    }
}
