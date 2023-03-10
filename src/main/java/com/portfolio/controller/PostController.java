package com.portfolio.controller;

import com.portfolio.request.common.Page;
import com.portfolio.request.post.*;
import com.portfolio.request.validator.post.*;
import com.portfolio.response.post.*;
import com.portfolio.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.portfolio.repository.util.MemberUtil.validateUsername;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final CreatePostValidator createPostValidator;

    private final SearchSinglePostValidator searchSinglePostValidator;

    private final SearchPostsByBoardValidator searchPostsByBoardValidator;

    private final EditPostValidator editPostValidator;

    private final DeletePostValidator deletePostValidator;

    @InitBinder("createPost")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createPostValidator);
    }

    @InitBinder("searchSinglePost")
    public void initBinder124(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(searchSinglePostValidator);
    }

    @InitBinder("searchPostsByBoard")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(searchPostsByBoardValidator);
    }

    @InitBinder("editPost")
    public void initBinder32(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(editPostValidator);
    }

    @InitBinder("deletePost")
    public void initBinder4(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(deletePostValidator);
    }


    /** 작성 기능 */
    //글 단건 작성
    @PostMapping("/posts")
    public void post(@RequestBody @Validated CreatePost request) {
        postService.write(request);
    }

    /** 조회 기능 */
    //단건 조회
    @GetMapping("/posts")
    public SinglePostResponse singlePost(@Validated SearchSinglePost request) {
        return postService.findSinglePost(request);
    }

    //  전체글 페이징 조회
    @GetMapping("/posts/view")
    public List<PostResponse> postsList(Page request) {
        return postService.findPosts(request);
    }

    // 특정 게시판에 작성된 글 페이징 조회
    @GetMapping("/posts/board/view")
    public List<BoardPostResponse> boardPosts(@Validated SearchPostsByBoard request) {
        return postService.findPostsByBoard(request);
    }

    //특정 회원이 작성한 글 페이징 조회
    /** 탈퇴 회원의 작성 글도 조회 가능 */
    @GetMapping("/member/{username}/posts")
    public List<MemberPostResponse> findPostsByMember(@PathVariable String username, Page request) {
        return postService.findPostsByMember(username, request);
    }

    //특정 회원이 댓글단 글 페이징 조회
    /** 탈퇴 회원이 댓글단 글은 조회 불가능*/
    @GetMapping("/member/{username}/commentPosts")
    public List<MemberCommentPostResponse> findPostsCommentedByMember(@PathVariable String username, Page request) {
        return postService.findPostsCommentedByMember(username, request);
    }

    //내가 좋아요 누른 글 페이징 조회
     /** 해당 회원 본인만 확인 가능함. 타인이 조회 시도시 인가 예외 발생 */
    @GetMapping("/member/{username}/likes")
    public List<MyLikedPostResponse> findLikedPosts(@PathVariable String username, Page request) {
        validateUsername(username);
        return postService.findMyLikedPosts(request);
    }


    /** 수정 기능 */
    //글 단건 수정
    @PatchMapping("/posts")
    public void update(@Validated @RequestBody EditPost request) {
        postService.edit(request);
    }


    /** 삭제 기능 */
    //글 단건 삭제
    @DeleteMapping("/posts")
    public void delete(@Validated DeletePost request) {
        postService.delete(request);
    }
}