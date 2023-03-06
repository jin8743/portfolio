package com.portfolio.controller;

import com.portfolio.request.comment.*;
import com.portfolio.request.member.PageRequest;
import com.portfolio.request.validator.comment.CommentCreateValidator;
import com.portfolio.request.validator.comment.DeleteCommentValidator;
import com.portfolio.request.validator.comment.EditCommentValidator;
import com.portfolio.response.comment.MemberCommentResponse;
import com.portfolio.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CommentCreateValidator commentCreateValidator;

    private final EditCommentValidator editCommentValidator;

    private final DeleteCommentValidator deleteCommentValidator;

    @InitBinder("commentCreatePostIdRequest")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(commentCreateValidator);
    }

    @InitBinder("editCommentIdRequest")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(editCommentValidator);
    }

    @InitBinder("deleteCommentIdRequest")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(deleteCommentValidator);
    }

    //단건 작성
    @PostMapping("/comments")
    public void writeComment(CommentCreatePostIdRequest postId,
                             @RequestBody @Validated CommentCreateRequest request) {
        commentService.writeComment(postId, request);
    }

    //대댓글 단건 작성
    @PostMapping("/comments/child/")
    public void childComment(ParentCommentIdRequest parentCommentId,
                             @RequestBody @Validated CommentCreateRequest request) {
        commentService.createChild(parentCommentId, request);
    }


    //특정 member 작성 댓글 페이징 조회
    @GetMapping("/{username}/comments")
    public List<MemberCommentResponse> memberComments(@PathVariable String username,
                                                      PageRequest request) {
        return commentService.memberCommentList(username, request);
    }


    //단건 수정
    @PatchMapping("/comments")
    public void update(EditCommentIdRequest commentId, @RequestBody @Validated CommentEditRequest commentEdit, Authentication authentication) {
        commentService.edit(commentId, commentEdit);
    }

    //단건 삭제
    @DeleteMapping("/comments/")
    public void delete(EditCommentIdRequest commentId) {
        commentService.delete(commentId);
    }
}
