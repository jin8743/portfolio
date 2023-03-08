package com.portfolio.service;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.comment.*;
import com.portfolio.request.common.Page;
import com.portfolio.response.comment.MemberCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Comment.*;
import static com.portfolio.domain.editor.CommentEditor.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberUtil memberUtil;



    //특정 member 작성 댓글 페이징 조회
    public List<MemberCommentResponse> findCommentsByMember(String username, Page request) {
        Member member = memberUtil.getActiveMember(username);
        return commentRepository.findCommentsByMember(request.getPage(), member).stream()
                .map(MemberCommentResponse::new)
                .collect(Collectors.toList());
    }

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
        return createComment(null, member, parentComment, request.getContent());
    }

    //단건 수정
    @Transactional
    public void edit(EditComment request) {
        Comment comment = commentRepository.findCommentWithMemberById(request.getCommentId());
        editComment(request, comment);
    }

    //단건 삭제
    @Transactional
    public void delete(DeleteComment request) {
        Comment comment = commentRepository.findCommentWithMemberById(request.getId());
        commentRepository.delete(comment);
    }
}
