package com.portfolio.repository.like;

import com.portfolio.domain.Like;
import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {


    @Override
    @EntityGraph(attributePaths = {"post", "member"})
    Optional<Like> findById(Long Id);

    void deleteAllByPost(Post post);
}
