package com.portfolio.repository;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByPost(Post post);
}
