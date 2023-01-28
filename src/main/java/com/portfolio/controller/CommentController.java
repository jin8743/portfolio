package com.portfolio.controller;

import com.portfolio.request.CommentCreateRequest;
import com.portfolio.request.CommentEditRequest;
import com.portfolio.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/comments/{postId}")
    public void comment(@PathVariable Long postId, @RequestBody @Validated CommentCreateRequest commentCreate, Authentication authentication) {
        commentService.save(postId, commentCreate, authentication.getName());
    }

    @PatchMapping("/comments/{commentId}")
    public void update(@PathVariable Long commentId, @RequestBody @Validated CommentEditRequest commentEdit, Authentication authentication) {
        commentService.edit(commentId, commentEdit, authentication.getName());
    }
}
