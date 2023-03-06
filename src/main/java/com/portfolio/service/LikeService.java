package com.portfolio.service;

import com.portfolio.domain.Like;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.domain.Like.*;
import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberUtil memberUtil;
    private final PostRepository postRepository;

    @Transactional
    public void like(Long postId) {

        Member member = memberUtil.getContextMember();
        Post post = postRepository.findConcurrentById(postId)
                .orElseThrow(() -> new CustomNotFoundException(POST_NOT_FOUND));
        Like like = likeRepository.loadExistingLike(member, post);

        /** 좋아요가 이미 있는 경우 기존 좋아요 삭제,  해당글의 좋아요수 -1
         *  없는 경우 새로 생성, 해당글의 좋아요수 + 1
         */
        if (like == null) {
            likeRepository.save(createLike(post, member));
            post.increaseLike();
        } else {
            cancelLike(like);
            post.decreaseLike();
        }
    }

    @Transactional
    public void cancelLike(Like like) {
        likeRepository.delete(like);
    }


}
