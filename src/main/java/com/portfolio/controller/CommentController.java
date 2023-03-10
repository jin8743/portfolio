package com.portfolio.controller;

import com.portfolio.request.comment.*;
import com.portfolio.request.common.Page;
import com.portfolio.request.validator.comment.*;
import com.portfolio.response.comment.MyCommentResponse;
import com.portfolio.response.comment.PostCommentResponse;
import com.portfolio.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.portfolio.repository.util.MemberUtil.validateUsername;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final CreateCommentValidator createCommentValidator;

    private final CreateChildCommentValidator createChildCommentValidator;

    private final SearchCommentsInPostValidator searchCommentsInPostValidator;

    private final EditCommentValidator editCommentValidator;

    private final DeleteCommentValidator deleteCommentValidator;

    @InitBinder("createComment")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createCommentValidator);
    }

    @InitBinder("createChildComment")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createChildCommentValidator);
    }

    @InitBinder("searchCommentsInPost")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(searchCommentsInPostValidator);
    }

    @InitBinder("editComment")
    public void initBinder4(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(editCommentValidator);
    }

    @InitBinder("deleteComment")
    public void initBinder5(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(deleteCommentValidator);
    }

    /** 댓글 작성 */
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


    /** 댓글 조회 */

    //특정 글에 작성된 댓글과 대댓글 목록 조회
    /** 댓글 과 대댓글 삭제 여부에 따라 조회되는 형태의 경우
     *
     * 1. 해당 글이 삭제된 글인 경우 댓글과 대댓글 목록은 조회되지 않음
     * 2. Soft Delete 처리된 대댓글은 조회 되지 않음
     * 3. 댓글이 Soft Delete 처리 되었고 해당 댓글에 대댓글이 달리지 않았다면 조회 되지 않음
     * 4. 댓글이 Soft Delete 처리 되었으나 해당 댓글에 대댓글이 달려있는 경우 "삭제된 댓글입니다" 로 표시됨
     * 5. 댓글과 해당 댓글의 대댓글이 모두 Soft Delete 처리가 되었으면 해당 댓글과 대대댓글 모두 보여지지 않음
     *
     * EX)
     *   삭제된 댓글입니다   <-- 부모 댓글은 삭제되었으나 대댓글이 남아있는 경우 이렇게 표시됨
     *      ㄴ>   대댓글 1번
     *      ㄴ>   대댓글 2번
     *      ㄴ>   대댓글 3번
     *    만약 4번쨰 대댓글이 있었지만 삭제된 경우 보여지지 않음
     */
    @GetMapping("/comments")
    public List<PostCommentResponse> searchCommentsInPost(@Validated SearchCommentsInPost request) {
        return commentService.findCommentsInPost(request);
    }


    // 내가 작성한 댓글과 대댓글  페이징 조회 (다른 회원이 조회 시도시 인가 예외 발생)
    // 댓글이 작성된 글이 삭제된 경우에도 댓글 내용은 조회 가능
    // Soft Delete 된 댓글은 조회 불가
    /**
     * Ex)
     *   삭제된 글입니다
     *   ㄴ> 내가 작성한 댓글 1
     */
    @GetMapping("/member/{username}/comments")
    public List<MyCommentResponse> memberComments(@PathVariable String username, Page request) {
        validateUsername(username);
        return commentService.findCommentsByMember(request);
    }

    /** 댓글 수정 */
    //단건 수정
    @PatchMapping("/comments")
    public void update(@Validated @RequestBody EditComment request) {
        commentService.edit(request);
    }

    /** 댓글 삭제 */
    //단건 삭제
    @DeleteMapping("/comments")
    public void delete(@Validated DeleteComment request) {
        commentService.delete(request);
    }


}
