package com.portfolio.service;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.comment.*;
import com.portfolio.request.common.Page;
import com.portfolio.response.comment.MyCommentResponse;
import com.portfolio.response.comment.PostCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Comment.*;
import static com.portfolio.domain.editor.CommentEditor.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final MemberUtil memberUtil;


    /** 댓글 작성 */
    //단건 작성
    @Transactional
    public void writeComment(CreateComment request) {
        commentRepository.save(createNewComment(request));
    }

    private Comment createNewComment(CreateComment request) {
        Member member = memberUtil.getContextMember();
        Post post = postRepository.findPostById(request.getPostId());
        return createComment(post, member, null, request.getContent());
    }

    //대댓글 단건 작성
    @Transactional
    public void writeChildComment(CreateChildComment request) {
        commentRepository.save(createNewChildComment(request));
    }

    private Comment createNewChildComment(CreateChildComment request) {
        Member member = memberUtil.getContextMember();
        Comment parentComment = commentRepository.findCommentWithPostById(request.getParentCommentId());
        Post post = parentComment.getPost();
        return createComment(post, member, parentComment, request.getContent());
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
    public List<PostCommentResponse> findCommentsInPost(SearchCommentsInPost request) {
        Post post = postRepository.findPostById(request.getId());
        return commentRepository.findCommentsInPost(post, request.getPage()).stream()
                .map(PostCommentResponse::new).collect(Collectors.toList());
    }

    // 내가 작성한 댓글과 대댓글  페이징 조회 (다른 회원이 조회 시도시 인가 예외 발생)
    // 댓글이 작성된 글이 삭제된 경우에도 댓글 내용은 조회 가능
    // Soft Delete 된 댓글은 조회 불가
    /**
     * Ex)
     *   삭제된 글입니다
     *   ㄴ> 내가 작성한 댓글 1
     */
    public List<MyCommentResponse> findCommentsByMember(Page request) {
        Member member = memberUtil.getContextMember();
        return commentRepository.findMyComments(request.getPage(), member).stream()
                .map(MyCommentResponse::new)
                .collect(Collectors.toList());
    }

    /** 댓글 수정 */
    //단건 수정
    @Transactional
    public void edit(EditComment request) {
        Comment comment = commentRepository.findCommentById(request.getCommentId());
        editComment(request, comment);
    }

    /** 댓글 삭제 */
    //단건 삭제
    @Transactional
    public void delete(DeleteComment request) {
        Comment comment = commentRepository.findCommentById(request.getId());
        commentRepository.delete(comment);
    }
}
