package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.common.Page;
import com.portfolio.request.post.*;
import com.portfolio.response.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.editor.PostEditor.editPost;
import static com.portfolio.request.post.CreatePost.createPost;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
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
    //단건 조회 (탈퇴한 회원의 글 조회 가능)
    public SinglePostResponse findSinglePost(SearchSinglePost request) {
        Post post = postRepository.findPostWithMemberAndBoardById(request.getId());

        return new SinglePostResponse(post);
    }

    //전체글 페이징 조회
    public List<PostResponse> findPosts(Page request) {
        return postRepository.findAllPosts(request.getPage()).stream()
                .map(PostResponse::new).collect(Collectors.toList());
    }

    //특정 게시판에 작성된 글 페이징 조회 (탈퇴한 회원의 글 조회 불가)
    public List<BoardPostResponse> findPostsByBoard(SearchPostsByBoard request) {
        List<Post> posts = postRepository.findPostsByBoard(request);

       return posts.isEmpty() ? new ArrayList<>() : posts.stream().map(BoardPostResponse::new)
               .collect(Collectors.toList());
    }

    //특정 회원의 작성글 페이징 조회 (삭제된 글은 조회되지 않음, 탈퇴한 회원의 작성글도 조회 가능)
    public List<MemberPostResponse> findPostsByMember(String username, Page request) {
        Member member = memberUtil.getMember(username);
        List<Post> posts = postRepository.findPostsByMember(member, request.getPage());

        return posts.isEmpty() ? new ArrayList<>() : posts.stream().map(MemberPostResponse::new)
                .collect(Collectors.toList());
    }

    //특정 회원이 댓글단 글 페이징 조회 (Soft Delete 처리된 글은 조회되지 않음, 탈퇴한 회원이 댓글단글 조회 불가)
    public List<MemberCommentPostResponse> findPostsCommentedByMember(String username, Page page) {
        Member member = memberUtil.getActiveMember(username);
        List<Post> posts = postRepository.findPostsCommentedMyMember(member, page.getPage());

        return posts.isEmpty() ? new ArrayList<>() : posts.stream().map(MemberCommentPostResponse::new)
                .collect(Collectors.toList());
    }

    //내가 좋아요 누른글 페이징 조회 (Soft Delete 처리된 글은 조회되지 않음)
    /** 현재 사용자 본인만 조회 가능. 타인이 조회 시도시 인가 예외 발생 */
    public List<MyLikedPostResponse> findMyLikedPosts(Page request) {
        List<Post> posts = postRepository.findMyLikedPosts(request.getPage());
        return posts.isEmpty() ? new ArrayList<>() : posts.stream().map(MyLikedPostResponse::new)
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

    //글 삭제
    @Transactional
    public void delete(DeletePost request) {
        Post post = postRepository.findPostWithMemberById(request.getId());
        postRepository.delete(post);
    }
}
