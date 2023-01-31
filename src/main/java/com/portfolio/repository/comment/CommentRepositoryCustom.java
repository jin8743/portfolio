package com.portfolio.repository.comment;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;
import com.portfolio.response.CommentResponse;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> getList(int page, Member member);
}
