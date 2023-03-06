package com.portfolio.repository.like;

import com.portfolio.domain.Like;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface LikeRepositoryCustom {


    Like loadExistingLike(Member member, Post post);

}
