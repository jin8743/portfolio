package com.portfolio.repository;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.response.PostResponse;

import java.util.List;

public interface PostRepositoryCustom {


    List<Post> getList(int page, Member member);

}
