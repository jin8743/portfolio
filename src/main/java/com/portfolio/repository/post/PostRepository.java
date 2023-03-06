package com.portfolio.repository.post;

import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {


    boolean existsById(Long id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Post> findConcurrentById(Long id);

    @EntityGraph(attributePaths = {"member"})
    Post findPostWithMemberById(Long id);

    Post findPostById(Long id);
}

