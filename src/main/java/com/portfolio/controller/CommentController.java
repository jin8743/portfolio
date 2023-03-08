package com.portfolio.controller;

import com.portfolio.request.comment.*;
import com.portfolio.request.common.Page;
import com.portfolio.request.validator.comment.CreateChildCommentValidator;
import com.portfolio.request.validator.comment.CreateCommentValidator;
import com.portfolio.request.validator.comment.DeleteCommentValidator;
import com.portfolio.request.validator.comment.EditCommentValidator;
import com.portfolio.response.comment.MemberCommentResponse;
import com.portfolio.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CreateCommentValidator createCommentValidator;

    private final CreateChildCommentValidator createChildCommentValidator;

    private final EditCommentValidator editCommentValidator;

    private final DeleteCommentValidator commentDeleteValidator;

    @InitBinder("createComment")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createCommentValidator);
    }

    @InitBinder("createChildComment")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createChildCommentValidator);
    }

    @InitBinder("editComment")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(editCommentValidator);
    }

    @InitBinder("deleteComment")
    public void initBinder4(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(commentDeleteValidator);
    }

    /** 작성 */
    //단건 작성
    @PostMapping("/comments")
    public void writeComment(@Validated @RequestBody CreateComment request) {
        commentService.writeComment(request);
    }

    //대댓글 단건 작성
    @PostMapping("/comments/child")
    public void childComment(@Validated @RequestBody CreateChildComment request) {
        commentService.writeChildComment(request);
    }


    /** 조회 */
    //특정 member 작성 댓글 페이징 조회
    @GetMapping("/member/{username}/comments")
    public List<MemberCommentResponse> memberComments(@PathVariable String username, Page request) {
        return commentService.findCommentsByMember(username, request);
    }

    /** 수정 */
    //단건 수정
    @PatchMapping("/comments")
    public void update(@Validated @RequestBody EditComment request) {
        commentService.edit(request);
    }

    /** 삭제 */
    //단건 삭제
    @DeleteMapping("/comments")
    public void delete(@Validated DeleteComment request) {
        commentService.delete(request);
    }
}
