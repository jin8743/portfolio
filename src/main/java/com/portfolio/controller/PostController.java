package com.portfolio.controller;

import com.portfolio.request.member.PageRequest;
import com.portfolio.request.post.*;
import com.portfolio.response.post.BoardPostResponse;
import com.portfolio.response.post.MemberPostResponse;
import com.portfolio.response.post.SinglePostResponse;
import com.portfolio.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    /** 조회 기능 */
    //글 단건 조회
    @GetMapping("/board/view")
    public SinglePostResponse singlePost(PostSearchRequest postSearch) {
        return postService.get(postSearch);
    }


    // 특정 게시판 글 페이징 조회
    @GetMapping("/board/lists")
    public List<BoardPostResponse> boardPosts(BoardSearchRequest boardSearch) {
        return postService.boardPostList(boardSearch);
    }


    //특정 member 글 페이징 조회
    @GetMapping("/{username}/post")
    public List<MemberPostResponse> memberPosts(@PathVariable String username,
                                                PageRequest page) {

        return postService.memberPostList(username, page);
    }


    /** 작성 기능 */
    //글 단건 작성
    @PostMapping("/board/write")
    public void post(@RequestParam String id,
                     @RequestBody @Validated PostCreateRequest postCreate) {
        postService.write(id, postCreate);
    }

    /**
     * 수정 기능
     */
//    @GetMapping("/board/modify")
//    public void beforeUpdate(PostSearchRequest postSearchRequest) {
//        postService.
//    }


    //글 단건 수정
    @PatchMapping("/board/modify")
    public void update(PostSearchRequest postSearch,
                       @RequestBody @Validated PostEditRequest postEditRequest) {

        postService.edit(postSearch, postEditRequest);
    }

    /** 삭제 기능 */
    //글 단건 삭제
    @DeleteMapping("/board/delete")
    public void delete(@ModelAttribute PostSearchRequest postSearch) {
        postService.delete(postSearch);
    }
}