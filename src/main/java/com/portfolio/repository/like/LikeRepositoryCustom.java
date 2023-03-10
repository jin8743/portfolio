package com.portfolio.repository.like;

import com.portfolio.domain.Post;

import java.util.List;

public interface LikeRepositoryCustom {




    /** 특정 글에 달린 좋아요수 count */
    Long findLikeCountByPost(Post post);


    /**현재 접속중인 회원이 해당글에 좋아요를 눌렀는지 여부 확인*/
    Boolean pressedLikeOnThisPost(Post post);


}
