package com.portfolio.repository.comment;

import com.portfolio.domain.Comment;
import com.portfolio.domain.Member;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> getList(int page, Member member);
}
