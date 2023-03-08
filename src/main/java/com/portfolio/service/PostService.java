package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.common.Page;
import com.portfolio.request.post.*;
import com.portfolio.response.post.BoardPostResponse;
import com.portfolio.response.post.MemberPostResponse;
import com.portfolio.response.post.PostBeforeEditResponse;
import com.portfolio.response.post.SinglePostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.editor.PostEditor.editPost;
import static com.portfolio.repository.util.MemberUtil.*;
import static com.portfolio.request.post.CreatePost.createPost;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final MemberUtil memberUtil;


    /** 작성 기능 */
    @Transactional
    public void write(CreatePost request) {
        postRepository.save(createNewPost(request));
    }

    private Post createNewPost(CreatePost request) {
        Member member = memberUtil.getContextMember();
        Board board = boardRepository.findByBoardName(request.getBoardName());
        return createPost(member, board, request);
    }

    /** 조회 기능 */
    //단건 조회
    //TODO  내 댓글인지 여부 추가

    public SinglePostResponse findSinglePost(SearchSinglePost request) {
        Post post = postRepository.findSinglePostWithId(request.getId());
        return new SinglePostResponse(post, getAuthenticatedUsername());
    }

    //특정 게시판 글 페이징 조회
    public List<BoardPostResponse> findPostsByBoard(SearchPostsByBoard request) {
        List<Post> posts = postRepository.findPostsByBoard(request);
       return posts.isEmpty() ? new ArrayList<>() : posts.stream().map(BoardPostResponse::new)
               .collect(Collectors.toList());
    }

    //특정 member 작성글 페이징 조회
    public List<MemberPostResponse> findPostsByMember(String username, Page request) {
        Member member = memberUtil.getActiveMember(username);
        List<Post> posts = postRepository.findPostsByMember(member, request.getPage());
        return posts.isEmpty() ? new ArrayList<>() : posts.stream().map(MemberPostResponse::new)
                .collect(Collectors.toList());
    }

    /** 수정 기능 */

    //글 수정
    @Transactional
    public void edit(EditPost request) {
        Post post = postRepository.findPostWithMemberById(request.getPostId());
        editPost(request, post);
    }


    /** 삭제 기능 */
    //TODO 댓글, 좋아요 삭제
    @Transactional
    public void delete(DeletePost request) {
        Post post = postRepository.findPostWithMemberById(request.getId());
        likeRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }
}
