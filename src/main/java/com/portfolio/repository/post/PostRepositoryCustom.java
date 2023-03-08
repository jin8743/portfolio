package com.portfolio.repository.post;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.request.post.SearchPostsByBoard;

import java.util.List;

public interface PostRepositoryCustom {


    Post findSinglePostWithId(Long id);

    List<Post> findPostsByBoard(SearchPostsByBoard searchPostsByBoard);

    List<Post> findPostsByMember(Member member, int page);

}
