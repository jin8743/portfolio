package com.portfolio.controller.factory;

import com.portfolio.domain.Like;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.like.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeFactory {

    @Autowired
    private LikeRepository likeRepository;

    public Like createLike(Post post, Member member) {

        Like like = Like.builder()
                .post(post)
                .member(member)
                .build();

        likeRepository.save(like);

        return like;
    }
}
