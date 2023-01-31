package com.portfolio.controller;

import com.portfolio.request.post.PostCreateRequest;
import com.portfolio.request.post.PostEditRequest;
import com.portfolio.response.PostResponse;
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
    public PostResponse singlePost(@RequestParam String id, @RequestParam Long no) {
        return postService.get(id, no);
    }

    //단건 작성
    @PostMapping("/board/write")
    public void post(@RequestParam String id, @RequestBody @Validated PostCreateRequest postCreate) {
        postService.write(id, postCreate);
    }

    //전체 조회
    @GetMapping("/board/lists")
    public List<PostResponse> getList(@RequestParam String id, @RequestParam int page) {
        return postService.getList(id, page);
    }

    //단건 수정
    @PatchMapping("/board/modify")
    public void update(@RequestParam String id, @RequestParam Long no, @RequestBody @Validated PostEditRequest postEdit) {
        postService.edit(id, no, postEdit);
    }

    //단건 삭제
    @DeleteMapping("/board/delete")
    public void delete(@RequestParam String id, @RequestParam Long no) {
        postService.delete(id, no);
    }

    //특정 member가 작성한 글 전제 조회
    @GetMapping("/{username}/posts")
    public List<PostResponse> myPosts(@PathVariable String username, @RequestParam int page) {
        return postService.getListWithUsername(username, page);
    }
}