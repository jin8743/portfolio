package com.portfolio.repository.like;

import com.portfolio.domain.Like;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {


    void deleteAllByPost(Post post);

    Like findByPostAndMember(Post post, Member member);


    /** 테스트 케이스에서 사용하는 조회용 method */
    @EntityGraph(attributePaths = {"post", "member"})
    Like findWithPostAndMemberById(Long id);
}
