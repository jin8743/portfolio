package com.portfolio.controller;

import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.request.post.PostEditRequest;
import com.portfolio.request.post.BoardSearchRequest;
import com.portfolio.request.post.PostSearchRequest;
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


    //단건 조회
    @GetMapping("/board/view")
    public SinglePostResponse singlePost(@ModelAttribute PostSearchRequest singlePost) {
        return postService.get(singlePost);
    }

    // 특정 게시판 글 조회
    @GetMapping("/board/lists")
    public List<BoardPostResponse> getList(@ModelAttribute BoardSearchRequest boardSearch) {
        return postService.boardPostList(boardSearch);
    }

    //특정 member가 작성한 글 전제 조회
    @GetMapping("/{username}/post")
    public List<MemberPostResponse> myPosts(@PathVariable String username, @RequestParam(defaultValue = "1") Integer page) {
        return postService.memberPostList(username, page);
    }

    //단건 작성
    @PostMapping("/board/write")
    public void post(@RequestParam String id, @RequestBody @Validated PostCreateRequest postCreate) {
        postService.write(id, postCreate);
    }

    //단건 수정
    @PatchMapping("/board/modify")
    public void update(@ModelAttribute PostSearchRequest postSearch,
                       @RequestBody @Validated PostEditRequest postEdit) {

        postService.edit(postSearch, postEdit);
    }

    //단건 삭제
    @DeleteMapping("/board/delete")
    public void delete(@ModelAttribute PostSearchRequest postSearch) {
        postService.delete(postSearch);
    }
}