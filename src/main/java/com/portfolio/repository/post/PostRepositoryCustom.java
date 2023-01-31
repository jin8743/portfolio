package com.portfolio.repository.post;

import com.portfolio.domain.Board;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.response.PostResponse;

import java.util.List;

public interface PostRepositoryCustom {


    List<Post> getList(Board board, int page, Member member);

}
