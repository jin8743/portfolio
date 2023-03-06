package com.portfolio.controller;

import com.portfolio.request.member.PageRequest;
import com.portfolio.request.post.*;
import com.portfolio.request.validator.post.BoardSearchValidator;
import com.portfolio.request.validator.post.DeletePostValidator;
import com.portfolio.request.validator.post.EditPostValidator;
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

    private final BoardSearchValidator boardSearchValidator;

    private final EditPostValidator editPostValidator;

    private final DeletePostValidator deletePostValidator;

    @InitBinder("boardSearchRequest")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardSearchValidator);
    }

    @InitBinder("editPostIdRequest")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(editPostValidator);
    }

    @InitBinder("deletePostIdRequest")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(deletePostValidator);
    }

    /** 작성 기능 */
    //글 단건 작성
    @PostMapping("/write")
    public void post(@RequestBody @Validated PostCreateRequest request) {
        postService.write(request);
    }


    /**
     * 조회 기능
     */
    @GetMapping("/board/view")
    public SinglePostResponse singlePost(PostIdRequest request) {
        return postService.getSinglePost(request);
    }


    // 특정 게시판에 작성된 글 페이징 조회
    @GetMapping("/board/lists")
    public List<BoardPostResponse> boardPosts(@Validated BoardSearchRequest request) {
        return postService.boardPostList(request);
    }


    //특정 member 가 작성한 글 페이징 조회
    @GetMapping("/member/{username}/post")
    public List<MemberPostResponse> memberPosts(@PathVariable String username,
                                                PageRequest page) {
        return postService.memberPostList(username, page);
    }


    /**
     * 수정 기능
     */
    //수정전 글 조회
    @GetMapping("/modify")
    public PostBeforeEditResponse findPostToEdit(@Validated EditPostIdRequest request) {
       return postService.findPostToEdit(request);
    }

    //글 단건 수정
    @PatchMapping("/modify")
    public void update(@Validated EditPostIdRequest postId,
                       @RequestBody @Validated PostEditRequest request) {
        postService.edit(postId, request);
    }

    /** 삭제 기능 */
    //글 단건 삭제
    @DeleteMapping("/delete")
    public void delete(@Validated DeletePostIdRequest request) {
        postService.delete(request);
    }
}