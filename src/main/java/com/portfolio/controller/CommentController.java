package com.portfolio.controller;

import com.portfolio.request.comment.CommentCreateRequest;
import com.portfolio.request.comment.CommentEditRequest;
import com.portfolio.response.CommentResponse;
import com.portfolio.response.PostResponse;
import com.portfolio.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //단건 작성
    @PostMapping("/comments/{postId}")
    public void comment(@PathVariable Long postId, @RequestBody @Validated CommentCreateRequest commentCreate, Authentication authentication) {
        commentService.write(postId, commentCreate, authentication.getName());
    }

    //단건 조회
    @GetMapping("/comments/{commentId}")
    public CommentResponse singleComment(@PathVariable Long commentId) {
        return commentService.get(commentId);
    }

    //단건 수정
    @PatchMapping("/comments/{commentId}")
    public void update(@PathVariable Long commentId, @RequestBody @Validated CommentEditRequest commentEdit, Authentication authentication) {
        commentService.edit(commentId, commentEdit, authentication.getName());
    }

    //단건 삭제
    @DeleteMapping("/comments/{commentId}")
    public void delete(@PathVariable Long commentId, Authentication authentication) {
        commentService.delete(commentId, authentication.getName());
    }

    //자신이 작성한 댓글 전체 조회
    @GetMapping("/myPage/comments")
    public List<CommentResponse> myComments(@RequestParam int page, Authentication authentication) {
        return commentService.getMyList(page, authentication.getName());
    }
}
