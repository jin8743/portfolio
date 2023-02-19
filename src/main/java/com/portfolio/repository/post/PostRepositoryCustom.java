package com.portfolio.repository.post;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.request.post.BoardSearchRequest;

import java.util.List;
import java.util.ListResourceBundle;

public interface PostRepositoryCustom {


    Post findWithId(Long id);

    Post findValidationPost(Long id);

    List<Post> boardList(BoardSearchRequest boardSearchRequest);

    List<Post> memberList(Member member, int page);

}
