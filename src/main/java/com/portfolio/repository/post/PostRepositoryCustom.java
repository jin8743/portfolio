package com.portfolio.repository.post;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.request.post.BoardSearchRequest;

import java.util.List;

public interface PostRepositoryCustom {


    Post findAllWithId(Long id);

    List<Post> boardList(BoardSearchRequest boardSearchRequest);

    List<Post> memberList(Member member, int page);
}
