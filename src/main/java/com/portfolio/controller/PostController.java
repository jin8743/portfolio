package com.portfolio.controller;

import com.portfolio.request.common.Page;
import com.portfolio.request.post.*;
import com.portfolio.request.validator.post.*;
import com.portfolio.response.post.BoardPostResponse;
import com.portfolio.response.post.MemberPostResponse;
import com.portfolio.response.post.PostBeforeEditResponse;
import com.portfolio.response.post.SinglePostResponse;
import com.portfolio.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final CreatePostValidator createPostValidator;

    private final SearchSinglePostValidator searchSinglePostValidator;

    private final SearchPostsByBoardValidator searchPostsByBoardValidator;

    private final SearchPostToEditValidator searchPostToEditValidator;

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

    @InitBinder("searchPostToEdit")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(searchPostToEditValidator);
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


    // 특정 게시판에 작성된 글 페이징 조회
    @GetMapping("/posts/view")
    public List<BoardPostResponse> boardPosts(@Validated SearchPostsByBoard request) {
        return postService.findPostsByBoard(request);
    }


    //특정 member 가 작성한 글 페이징 조회
    @GetMapping("/member/{username}/posts")
    public List<MemberPostResponse> memberPosts(@PathVariable String username, Page page) {
        return postService.findPostsByMember(username, page);
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