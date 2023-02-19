package com.portfolio.repository.comment;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    void deleteAllByPost(Post post);
}
