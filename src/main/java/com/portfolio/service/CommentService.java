package com.portfolio.service;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.comment.*;
import com.portfolio.request.member.PageRequest;
import com.portfolio.response.comment.MemberCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Comment.*;
import static com.portfolio.domain.editor.CommentEditor.*;
import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberUtil memberUtil;



    //특정 member 작성 댓글 페이징 조회
    public List<MemberCommentResponse> memberCommentList(String username, PageRequest request) {
        Member member = memberUtil.getActiveMember(username);
        return commentRepository.findByMember(request.getPage(), member).stream()
                .map(MemberCommentResponse::new).collect(Collectors.toList());
    }

    //단건 작성
    @Transactional
    public void writeComment(CommentCreatePostIdRequest postIdRequest, CommentCreateRequest request) {
        commentRepository.save(createNewComment(postIdRequest, request));
    }

    private Comment createNewComment(CommentCreatePostIdRequest postIdRequest, CommentCreateRequest request) {
        Member member = memberUtil.getContextMember();
        Post post = postRepository.findPostById(postIdRequest.getPostId());
        return createComment(post, member, request.getContent());
    }

    //대댓글 단건 작성
    @Transactional
    public void createChild(ParentCommentIdRequest commentId, CommentCreateRequest request) {
        commentRepository.save(createNewChildComment(commentId, request));
    }
    private Comment createNewChildComment(ParentCommentIdRequest commentId, CommentCreateRequest request) {
        Member member = memberUtil.getContextMember();
        Comment parentComment = commentRepository.findCommentWithPostById(commentId.getCommentId())
                .orElseThrow(() -> new CustomNotFoundException(COMMENT_NOT_FOUND));
        return createChildComment(member, parentComment, request.getContent());
    }

    //단건 수정
    @Transactional
    public void edit(EditCommentIdRequest commentId, CommentEditRequest commentEdit) {
        Comment comment = commentRepository.findCommentWithMemberById(commentId.getId());
        editComment(commentEdit, comment);
    }

    //단건 삭제
    @Transactional
    public void delete(EditCommentIdRequest commentId) {
        Comment comment = commentRepository.findCommentWithMemberById(commentId.getId());
        commentRepository.delete(comment);
    }
}
