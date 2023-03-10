package com.portfolio.controller;

import com.portfolio.request.board.CreateBoard;
import com.portfolio.request.comment.DeleteComment;
import com.portfolio.request.common.Page;
import com.portfolio.request.post.DeletePost;
import com.portfolio.request.validator.board.BoardCreateValidator;
import com.portfolio.request.validator.comment.DeleteCommentValidator;
import com.portfolio.request.validator.post.DeletePostValidator;
import com.portfolio.response.member.MemberProfileForAdminResponse;
import com.portfolio.service.BoardService;
import com.portfolio.service.CommentService;
import com.portfolio.service.MemberService;
import com.portfolio.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final BoardService boardService;

    private final MemberService memberService;

    private final PostService postService;

    private final CommentService commentService;

    private final BoardCreateValidator boardCreateValidator;

    private final DeletePostValidator deletePostValidator;

    private final DeleteCommentValidator deleteCommentValidator;



    @InitBinder("createBoard")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardCreateValidator);
    }

    @InitBinder("deletePost")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(deletePostValidator);
    }

    @InitBinder("deleteComment")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(deleteCommentValidator);
    }



    /*** 게시판 생성 */
    @PostMapping("/admin/board")
    public void createBoard(@Validated @RequestBody CreateBoard request) {
        boardService.create(request);
    }

    /**
     * 회원 조회 (탈퇴한 회원 포함)
     */
    //회원 목록 페이징 조회
    @GetMapping("/admin/members")
    public List<MemberProfileForAdminResponse> searchMemberList(Page page) {
        return memberService.loadMemberListForAdmin(page);
    }

    // 회원 1명  조회
    @GetMapping("/admin/members/{username}")
    public MemberProfileForAdminResponse searchMember(@PathVariable String username) {
        return memberService.loadMemberForAdmin(username);
    }

    /**
     * 삭제
     */
    // 글 단건 삭제
    @DeleteMapping("/admin/posts")
    public void deletePostByAdmin(@Validated DeletePost request) {
        postService.delete(request);
    }

    // 회원 강퇴
    @DeleteMapping("/admin/members/{username}")
    public void deleteMemberByAdmin(@PathVariable String username) {
        memberService.unregisterByAdmin(username);
    }

    // 댓글 단건 삭제
    @DeleteMapping("/admin/comments")
    public void deleteCommentByAdmin(@Validated DeleteComment request) {
        commentService.delete(request);
    }
}
