package com.portfolio.repository.comment;

import com.portfolio.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {


    @EntityGraph(attributePaths = {"post"})
    Comment findCommentWithPostById(Long id);

    @EntityGraph(attributePaths = "member")
    Comment findCommentWithMemberById(Long id);

    Comment findCommentById(Long id);


    @EntityGraph(attributePaths = {"member", "post"})
    Comment findCommentWithMemberAndPostById(Long id);


    /** 테스트 케이스용 method */
    @EntityGraph(attributePaths = {"member", "post"})
    Comment findWithMemberAndPostById(Long id);

    /** 테스트 케이스용 method */
    @EntityGraph(attributePaths = {"childs", "post"})
    Comment findWithChildCommentsById(Long id);
}
