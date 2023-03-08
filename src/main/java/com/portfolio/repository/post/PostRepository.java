package com.portfolio.repository.post;

import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {


    boolean existsById(Long id);

    @EntityGraph(attributePaths = {"member"})
    Post findPostWithMemberById(Long id);

    Post findPostById(Long id);

    @EntityGraph(attributePaths = {"member", "board"})
    Post findPostWithMemberAndBoardById(Long id);
}

