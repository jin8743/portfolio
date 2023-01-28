package com.portfolio.controller;

import com.portfolio.request.PostCreateRequest;
import com.portfolio.request.PostEditRequest;
import com.portfolio.response.PostResponse;
import com.portfolio.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //단건 조회
    @GetMapping("/posts/{postId}")
    public PostResponse singlePost(@PathVariable Long postId) {
        return postService.get(postId);
    }

    //단건 등록
    @PostMapping("/posts")
    public void post(@RequestBody @Validated PostCreateRequest postCreate, Authentication authentication) {
        postService.write(postCreate, authentication.getName());
    }

    //전체 조회
    @GetMapping("/posts")
    public List<PostResponse> getList(@RequestParam int page) {
        return postService.getList(page);
    }


    //단건 수정
    @PatchMapping("/posts/{postId}")
    public void update(@PathVariable Long postId, @RequestBody @Validated PostEditRequest postEdit, Authentication authentication) {
        postService.edit(postId, postEdit, authentication.getName());
    }

    //단건 삭제
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId, Authentication authentication) {
        postService.delete(postId, authentication.getName());
    }

    //자신이 작성한 글 전제 조회
    @GetMapping("/myPage/posts")
    public List<PostResponse> myPost(@RequestParam int page, Authentication authentication) {
        return postService.getMyList(page, authentication.getName());
    }
}